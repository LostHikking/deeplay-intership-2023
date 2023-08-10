package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import java.io.IOException;

public class GameRunner {
  /**
   * Запуск локальной игры.
   *
   * @throws IOException Ошибка ввода/вывода
   */
  public static void run() throws IOException {
    var board = new HashBoard();

    var aiPlayer = new AiPlayer("AI1", board, Color.WHITE, new ConsoleUi(System.in, System.out));
    var aiPlayer2 = new AiPlayer("AI2", board, Color.BLACK, new ConsoleUi(System.in, System.out));

    GameController gameController = new GameController(aiPlayer, aiPlayer2, ChessType.CLASSIC);
    gameController.beginPlay();

    while (!gameController.isGameOver()) {
      gameController.nextMove();
    }
  }

  public static void main(String[] args) throws IOException {
    GameRunner.run();
  }
}
