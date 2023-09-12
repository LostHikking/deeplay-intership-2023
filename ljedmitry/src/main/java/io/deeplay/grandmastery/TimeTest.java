package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeTest {
  private static final int COUNT_GAMES = 1;
  private static final DecimalFormat df = new DecimalFormat("#.###");

  public static void main(String[] args) {
    int countWin = 0;
    int countMove = 0;
    long totalStartTime = System.currentTimeMillis();
    double totalElapsedGameTime = 0;
    double totalElapsedMoveTime = 0;
    long maxMoveTime = -1;

    for (int i = 1; i <= COUNT_GAMES; i++) {
      try {
        GameController gameController =
            new GameController(
                new LjeDmitryBot(Color.WHITE, "minimax", 3),
                new LjeDmitryBot(Color.BLACK, "minimax2", 3));
        gameController.beginPlay(ChessType.CLASSIC);

        long startGameTime = System.currentTimeMillis();
        while (!gameController.isGameOver()) {
          if (gameController.getGameStatus() == GameState.WHITE_MOVE) {
            countMove++;
          }

          long startMoveTime = System.currentTimeMillis();
          gameController.nextMove();
          long endMoveTime = System.currentTimeMillis();
          long elapsedMove = endMoveTime - startMoveTime;

          if (gameController.getGameStatus() == GameState.BLACK_MOVE) {
            if (elapsedMove > maxMoveTime) {
              maxMoveTime = elapsedMove;
            }
            totalElapsedMoveTime += elapsedMove;
          }
        }
        long endGameTime = System.currentTimeMillis();
        totalElapsedGameTime += endGameTime - startGameTime;

        GameState status = gameController.getGameStatus();
        if (status == GameState.WHITE_WIN) {
          countWin++;
        }
        log.info("Complete: " + i + ", result: " + status);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    long totalEndTime = System.currentTimeMillis();
    double totalTime = totalEndTime - totalStartTime;
    log.info("##################################");
    log.info("Results:");
    log.info("Win games: " + countWin * 100 / COUNT_GAMES + "%");
    log.info("Total time " + COUNT_GAMES + " games: " + df.format(totalTime / 1000) + " с.");
    log.info("Average game time: " + df.format(totalElapsedGameTime / COUNT_GAMES) + " мс.");
    log.info("Average move time: " + df.format(totalElapsedMoveTime / countMove) + " мс.");
    log.info("Max move time: " + maxMoveTime + " мс.");
  }
}
