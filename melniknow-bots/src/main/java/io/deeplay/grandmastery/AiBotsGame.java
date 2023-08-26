package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.minimaximus.Minimaximus;

/** Главный класс, который запускает локальную игру в шахматы. */
public class AiBotsGame {
  private static final int COUNT_TESTS = 100;
  private static final boolean WITH_GUI = true;
  private static Gui gui;

  /**
   * Локальная игра в шахматы.
   *
   * @param args Аргументы командной строки (не используются).
   */
  public static void main(String[] args) {
    if (WITH_GUI) {
      gui = new Gui();
    }

    for (int i = 1; i <= COUNT_TESTS; i++) {
      try {
        var gameController =
            new GameController(new Minimaximus(Color.WHITE, 2), new AiPlayer(Color.BLACK));
        gameController.beginPlay(ChessType.CLASSIC);
        if (WITH_GUI) {
          gui.showBoard(gameController.getBoard(), Color.WHITE);
        }
        while (!gameController.isGameOver()) {
          gameController.nextMove();
          if (WITH_GUI) {
            gui.showBoard(gameController.getBoard(), Color.WHITE);
          }
        }

        var status = gameController.getGameStatus();
        System.out.println("Complete: " + i + ", result: " + status);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }
}
