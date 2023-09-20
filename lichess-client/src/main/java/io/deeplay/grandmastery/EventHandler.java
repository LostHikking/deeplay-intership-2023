package io.deeplay.grandmastery;

import chariot.model.Challenge;
import chariot.model.Enums;
import chariot.model.Event;
import chariot.model.GameEvent;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventHandler {
  public record OnlineGame(String id, Player player) {}

  private final LichessClient client;
  private final String botName;
  private final String id;

  protected final Map<String, OnlineGame> onlineGameMap = new HashMap<>();

  /**
   * Конструктор.
   *
   * @param client LichessClient
   * @param botName Имя бота
   */
  public EventHandler(LichessClient client, String botName) {
    this.client = client;
    this.botName = botName;
    this.id = client.getId();
  }

  /** Прерываем все игры. */
  public void clean() {
    log.info("Cleaning before exit");
    if (!onlineGameMap.isEmpty()) {
      log.info("Aborting all games in memory");
      onlineGameMap.keySet().forEach(client::abort);
      onlineGameMap.clear();
    }
  }

  /** Запускаем бота. */
  public void start() {
    log.info("Start playing as a bot");

    var events = client.streamEvents();
    log.info("Connection successful, waiting for challenges...");

    events.forEach(
        event -> {
          switch (event.type()) {
            case challenge -> newChallenge(event);
            case gameStart -> startGame(event);
            case challengeCanceled, challengeDeclined -> log.info(
                "Challenge cancelled / declined: {}", event);
            case gameFinish -> cleanFinishedGame(event);
            default -> log.info(event.type().name());
          }
        });
  }

  void newChallenge(Event event) {
    var challengeEvent = (Event.ChallengeEvent) event;
    log.info("New challenge received. Details: {}", challengeEvent.challenge());
    if (!isChallengeAcceptable(challengeEvent)) {
      declineChallenge(challengeEvent);
    } else {
      acceptChallenge(challengeEvent);
    }
  }

  boolean isChallengeAcceptable(Event.ChallengeEvent challengeEvent) {
    Predicate<Challenge> variantStandard =
        challenge -> challenge.variant().key() == Enums.GameVariant.standard;

    return variantStandard.test(challengeEvent.challenge());
  }

  void acceptChallenge(Event.ChallengeEvent challengeEvent) {
    log.info("Accepting challenge!");

    var color =
        challengeEvent.challenge().finalColor() != Enums.Color.white ? Color.WHITE : Color.BLACK;

    if (challengeEvent.challenge().challenger().id().equals(id)) {
      color = color.getOpposite();
    }

    var bot = BotFactory.create(botName, color);
    var board = new HashBoard();
    Boards.defaultChess().accept(board);
    bot.startup(board);

    var onlineGame = new OnlineGame(challengeEvent.id(), bot);
    onlineGameMap.put(onlineGame.id(), new OnlineGame(challengeEvent.id(), bot));

    client.acceptChallenge(challengeEvent.id());
  }

  void declineChallenge(Event.ChallengeEvent challengeEvent) {
    log.info("Challenge is not acceptable, declining...");
    client.declineChallenge(challengeEvent.id());
  }

  void cleanFinishedGame(Event event) {
    log.info("Game finished, cleaning memory: {}", event);
    onlineGameMap.remove(event.id());
  }

  void startGame(Event event) {
    log.info("Game to start: {}", event);

    if (!onlineGameMap.containsKey(event.id())) {
      log.warn("Game {} is not in memory, resigning", event.id());
      client.resign(event.id());
      return;
    }

    var onlineGame = onlineGameMap.get(event.id());

    log.info("Ready to play game with id {}, waiting for events...", event.id());
    var gameEvents = client.streamGameEvents(event.id());

    gameEvents.forEach(
        gameEvent -> {
          switch (gameEvent.type()) {
            case gameFull -> startPlayingGame(onlineGame, (GameEvent.Full) gameEvent);
            case gameState -> updateGameState(onlineGame, (GameEvent.State) gameEvent);
            default -> log.info(gameEvent.type().name());
          }
        });
  }

  void startPlayingGame(OnlineGame onlineGame, GameEvent.Full full) {
    log.info("Game starting: {}", full);
    playNextMove(onlineGame, true, null);
  }

  @SuppressWarnings("StringSplitter")
  void updateGameState(OnlineGame onlineGame, GameEvent.State state) {
    log.info("Game state: {}", state);

    var moves = state.moves().split(" ");
    var isWhite = moves.length % 2 == 0;
    var status = state.status();
    switch (status) {
      case started, created -> playNextMove(
          onlineGame, isWhite, LongAlgebraicNotation.getMoveFromString(moves[moves.length - 1]));
      default -> log.info(status.name());
    }
  }

  void playNextMove(OnlineGame onlineGame, boolean isWhite, Move lastMove) {
    if ((isWhite && onlineGame.player.getColor() == Color.WHITE)
        || (onlineGame.player.getColor() == Color.BLACK && !isWhite)) {
      if (lastMove != null) {
        onlineGame.player.makeMove(lastMove);
      }
      var move = onlineGame.player.createMove();
      if (move != null) {
        if (move.moveType() == MoveType.DEFAULT) {
          onlineGame.player.makeMove(move);
          sendMove(onlineGame.id(), LongAlgebraicNotation.moveToString(move));
        }
      }
    }
  }

  void sendMove(String gameId, String moveUci) {
    log.info("Sending move: {}", moveUci);
    client.move(gameId, moveUci);
  }
}
