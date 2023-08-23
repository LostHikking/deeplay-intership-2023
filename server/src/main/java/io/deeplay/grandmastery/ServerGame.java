package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.PlayerInfo;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import java.io.IOException;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ServerGame implements Runnable {
  private final ServerController serverController;
  private final GameController gameController;

  /** Конструктор для объектов типа ServerGame. */
  public ServerGame(Player playerOne, Player playerTwo, ChessType chessType, Socket socket) {
    this.serverController = new ServerController(new ServerDao(playerOne, playerTwo, socket));

    gameController = new GameController(playerOne, playerTwo);
    gameController.beginPlay(chessType);

    log.info("Игра создана");
  }

  @Override
  public void run() {
    try {
      serverController.notifyStartGame(gameController.getBoard());
      log.info("Игра запущенна");

      while (!gameController.isGameOver()) {
        try {
          PlayerInfo currentPlayer = gameController.getCurrentPlayer();
          gameController.nextMove();
          if (gameController.isGameOver()) {
            break;
          }

          if (currentPlayer != gameController.getCurrentPlayer()) {
            var color = gameController.getOpponentPlayer().getColor();
            serverController.notifySuccessMove(
                color, gameController.getGameHistory().getLastMove());

            log.info("Сделан ход цветом - " + color);
          }
        } catch (GameException e) {
          var color = gameController.getOpponentPlayer().getColor();
          serverController.notifyWrongMove(color);

          log.error("Некорректный ход цветом - " + color);
        } catch (ServerException e) {
          var color = gameController.getOpponentPlayer().getColor();
          var gameStatus = color == Color.WHITE ? GameState.WHITE_WIN : GameState.BLACK_WIN;
          serverController.sendResult(gameStatus, gameController.getGameHistory().getBoards());

          return;
        }
      }

      log.info("Игра закончена");

      serverController.sendResult(
          gameController.getGameStatus(), gameController.getGameHistory().getBoards());
      serverController.close();
    } catch (IOException e) {
      log.error("Возникла проблема с чтением/записью - " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
