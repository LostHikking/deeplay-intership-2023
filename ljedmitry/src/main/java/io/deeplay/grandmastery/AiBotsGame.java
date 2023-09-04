package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import lombok.extern.slf4j.Slf4j;

/** Главный класс, который запускает локальную игру в шахматы. */
@Slf4j
public class AiBotsGame {
  private static final int COUNT_TESTS = 100;
  private static final boolean WITH_GUI = false;
  private static Gui gui;

  // new HumanPlayer("Dima", Color.BLACK, gui)
  // new AiPlayer(Color.BLACK)
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
            new GameController(new LjeDmitryBot(Color.WHITE), new AiPlayer(Color.BLACK));
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

        GameState status = gameController.getGameStatus();
        log.info("Complete: " + i + ", result: " + status);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
