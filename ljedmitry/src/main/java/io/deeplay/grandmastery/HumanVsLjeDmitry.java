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
    int countWin = 0;

    for (int i = 1; i <= COUNT_TESTS; i++) {
      try {
        GameController gameController =
            new GameController(
                new LjeDmitryBot(Color.WHITE), new HumanPlayer("Dima", Color.BLACK, gui));
        gameController.beginPlay(ChessType.CLASSIC);
        while (!gameController.isGameOver()) {
          gameController.nextMove();
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
