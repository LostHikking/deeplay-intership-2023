package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiBotsGame {
  private static final int COUNT_TESTS = 100;
  private static final boolean WITH_GUI = true;
  private static Gui gui;

  public static void main(String[] args) {
    if (WITH_GUI) {
      gui = new Gui(true);
    }

    int countWin = 0;
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
        if (status == GameState.WHITE_WIN) {
          countWin++;
        }
        log.info("Complete: " + i + ", result: " + status);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    log.info("Result win games: " + countWin * 100 / COUNT_TESTS + "%");
  }
}
