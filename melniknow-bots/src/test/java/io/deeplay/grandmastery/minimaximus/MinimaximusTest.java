package io.deeplay.grandmastery.minimaximus;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MinimaximusTest {
  @Test
  void winTest() {
    var countTest = 10;
    var result = new ArrayList<GameState>();

    for (int i = 1; i <= countTest; i++) {
      var gameController =
          new GameController(new Minimaximus(Color.WHITE, 2), new AiPlayer(Color.BLACK));
      gameController.beginPlay(ChessType.CLASSIC);
      while (!gameController.isGameOver()) {
        gameController.nextMove();
      }

      result.add(gameController.getGameStatus());
    }

    Assertions.assertTrue(result.stream().allMatch(gameState -> gameState == GameState.WHITE_WIN));
  }
}
