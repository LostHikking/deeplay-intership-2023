package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.LjeDmitryBot;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.utils.Algorithms;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NegaMaxTest {
  private static final String ALGORITHM_NAME = "negamax";
  private static final int DEEP = 2;

  @Test
  void generalTest() {
    int countGame = 100;
    int winGame = 0;

    for (int i = 0; i < countGame; i++) {
      Color botColor = i < countGame / 2 ? Color.WHITE : Color.BLACK;
      GameState expectGameEnd = i < countGame / 2 ? GameState.WHITE_WIN : GameState.BLACK_WIN;
      GameController gameController =
          new GameController(
              new LjeDmitryBot(botColor, ALGORITHM_NAME, DEEP),
              new Randomus(Algorithms.inversColor(botColor)));
      gameController.beginPlay(ChessType.CLASSIC);

      while (!gameController.isGameOver()) {
        Assertions.assertTimeout(
            Duration.ofSeconds(5), gameController::nextMove, "Bot move timeout");
      }

      if (gameController.getGameStatus() == expectGameEnd) {
        winGame++;
      }
    }

    Assertions.assertEquals(countGame, winGame, "Game win count");
  }
}
