package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;

/** Главный класс, который запускает локальную игру в шахматы. */
public class AiBotsGame {
  private static final int COUNT_TESTS = 10;
  private static final boolean WITH_GUI = true;
  private static Gui gui;
  private static int winCount = 0;
  private static int drawCount = 0;
  private static int blackCount = 0;

  /**
   * Локальная игра в шахматы.
   *
   * @param args Аргументы командной строки (не используются).
   */
  public static void main(String[] args) {
    if (WITH_GUI) {
      gui = new Gui(true);
    }

    for (int i = 1; i <= COUNT_TESTS; i++) {
      try {
        GameController gameController =
            new GameController(
                new LjeDmitryBot(Color.WHITE, "newMinimax", 3),
                new LjeDmitryBot(Color.BLACK, "minimax", 3));
        gameController.beginPlay(ChessType.FISHERS);
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
        if (status == GameState.WHITE_WIN) {
          winCount++;
        } else if (status == GameState.BLACK_WIN) {
          blackCount++;
        } else if (status == GameState.DRAW) {
          drawCount++;
        }
        System.out.println("Complete: " + i + ", result: " + status);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
    System.out.println("Win white: " + (winCount * 100 / COUNT_TESTS) + "%");
    System.out.println("Win black: " + (blackCount * 100 / COUNT_TESTS) + "%");
    System.out.println("draw: " + (drawCount * 100 / COUNT_TESTS) + "%");
  }
}
