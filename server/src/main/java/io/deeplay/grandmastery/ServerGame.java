package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.StartGameResponse;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerGame implements Runnable {
  private final Player playerOne;
  private final Player playerTwo;
  private final ChessType chessType;
  private GameController gameController;

  // Нужен для отправки результата при игре Bot VS Bot
  private final Socket socket;

  /** Конструктор для объектов типа ServerGame. */
  public ServerGame(Player playerOne, Player playerTwo, ChessType chessType, Socket socket) {
    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
    this.chessType = chessType;
    this.socket = socket;

    log.info("Игра создана");
  }

  @Override
  public void run() {
    try {
      gameController = new GameController(playerOne, playerTwo);
      gameController.beginPlay(chessType);

      notifyStartGame();
      log.info("Игра запущенна");

      while (!gameController.isGameOver()) {
        try {
          gameController.nextMove();

          var color = gameController.getCurrentPlayer().getColor();
          notifySuccessMove(color);

          log.info("Сделан ход цветом - " + color);
        } catch (GameException e) {
          var color = gameController.getCurrentPlayer().getColor();
          notifyWrongMove(color);

          log.error("Некорректный ход цветом - " + color);
        }
      }

      log.info("Игра закончена");

      sendResult(gameController.getGameStatus());
      close();
    } catch (IOException e) {
      log.error("Возникла проблема с чтением/записью - " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private void notifyWrongMove(Color color) throws IOException {
    var json = ConversationService.serialize(new WrongMove());

    if (playerOne.getColor() == color && playerOne instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    } else if (playerTwo.getColor() == color && playerTwo instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    }
  }

  private void notifySuccessMove(Color color) throws IOException {
    var lastMove = gameController.getGameHistory().getLastMove();
    var json = ConversationService.serialize(new AcceptMove(lastMove));

    if (playerOne.getColor() != color && playerOne instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    } else if (playerTwo.getColor() != color && playerTwo instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    }
  }

  private void notifyStartGame() throws IOException {
    var board = gameController.getBoard();

    var startGameResponse = new StartGameResponse(Boards.getStringFromBoard(board));
    var json = ConversationService.serialize(startGameResponse);

    if (playerOne instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    }
    if (playerTwo instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    }
  }

  private void sendResult(GameState gameStatus) throws IOException {
    var boards = new ArrayList<String>();
    for (Board board : gameController.getGameHistory().getBoards()) {
      boards.add(Boards.getStringFromBoard(board));
    }

    var result = new ResultGame(gameStatus, boards);
    var json = ConversationService.serialize(result);

    if (playerOne instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    }
    if (playerTwo instanceof ServerPlayer serverPlayer) {
      ServerUtils.send(serverPlayer.getOut(), json);
    }
    if (socket != null) {
      try (var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8))) {
        ServerUtils.send(out, json);
      }
    }
  }

  /**
   * Функция закрывает ресурсы.
   *
   * @throws IOException В случае ошибки закрытия
   */
  public void close() throws IOException {
    if (playerOne instanceof ServerPlayer serverPlayer) {
      serverPlayer.getSocket().close();
    }
    if (playerTwo instanceof ServerPlayer serverPlayer) {
      serverPlayer.getSocket().close();
    }
    if (socket != null) {
      socket.close();
    }

    log.info("Соединение успешно закрыто!");
  }
}
