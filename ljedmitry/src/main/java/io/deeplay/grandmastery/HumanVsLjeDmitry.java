package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HumanVsLjeDmitry {
  private static final int COUNT_TESTS = 10;

  public static void main(String[] args) {
    Gui gui = new Gui(true);

    for (int i = 1; i <= COUNT_TESTS; i++) {
      try {
        GameController gameController =
            new GameController(
                new LjeDmitryBot(Color.WHITE), new HumanPlayer("Dima", Color.BLACK, gui));
        gameController.beginPlay(ChessType.CLASSIC);
        while (!gameController.isGameOver()) {
          long startMoveTime = System.currentTimeMillis();
          gameController.nextMove();
          long endMoveTime = System.currentTimeMillis();

          if (gameController.getGameStatus() == GameState.BLACK_MOVE) {
            log.info("Move time: " + (endMoveTime - startMoveTime) + " ms.");
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
