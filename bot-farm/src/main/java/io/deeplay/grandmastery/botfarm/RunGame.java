package io.deeplay.grandmastery.botfarm;

import io.deeplay.grandmastery.botfarm.utils.FarmUtils;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.WaitAnswerDraw;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunGame implements Runnable {
  private final GameHistory gameHistory = new GameHistory();

  @Getter private final Player player;
  @Getter private final BufferedReader in;
  @Getter private final BufferedWriter out;
  @Getter private final Board board;
  @Getter private final Socket socket;

  /**
   * Конструктор.
   *
   * @param player Игрок
   * @param socket Сокет
   * @param in BufferedReader
   * @param out BufferedWriter
   * @param board Доска
   */
  public RunGame(Player player, Socket socket, BufferedReader in, BufferedWriter out, Board board) {
    this.player = player;
    this.in = in;
    this.out = out;
    this.board = board;
    this.socket = socket;
  }

  @Override
  public void run() {
    gameHistory.startup(board);
    player.startup(board);

    try {
      IDto serverDto;

      do {
        var serverResponse = FarmUtils.getJsonFromServer(in);
        serverDto = ConversationService.deserialize(serverResponse);

        if (serverDto instanceof WaitMove) {
          makeMove();
        } else if (serverDto instanceof WrongMove) {
          gameHistory.getBoards().remove(gameHistory.getBoards().size() - 1);
          player.startup(gameHistory.getCurBoard());
        } else if (serverDto instanceof AcceptMove acceptMove) {
          player.makeMove(acceptMove.getMove());
          gameHistory.addBoard(player.getBoard());
        } else if (serverDto instanceof WaitAnswerDraw) {
          var json = ConversationService.serialize(new SendAnswerDraw(player.answerDraw()));
          FarmUtils.send(out, json);
        }
      } while (!(serverDto instanceof ResultGame));
    } catch (Exception e) {
      log.info("Игра завершена");
    }

    try {
      socket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void makeMove() throws IOException {
    Move move;
    while (true) {
      try {
        move = player.createMove();
        if (move != null) {
          if (move.moveType() == MoveType.DEFAULT) {
            player.makeMove(move);
            gameHistory.addBoard(player.getBoard());
            gameHistory.makeMove(move);
          }
          break;
        }
      } catch (GameException e) {
        if (e.getMessage().contains(GameErrorCode.GAME_ALREADY_OVER.getDescription())) {
          log.error("Игра уже завершилась!");
          return;
        }
      }
    }

    var dto = new SendMove(gameHistory.getLastMove());
    FarmUtils.send(out, ConversationService.serialize(dto));
  }
}
