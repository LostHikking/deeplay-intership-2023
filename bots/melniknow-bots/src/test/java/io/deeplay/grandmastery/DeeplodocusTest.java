package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeeplodocusTest {
  @Test
  void winTest() {
    var result = new ArrayList<GameState>();

    var gameController =
        new GameController(new Deeplodocus(Color.WHITE), new Randomus(Color.BLACK));
    gameController.beginPlay(ChessType.CLASSIC);
    while (!gameController.isGameOver()) {
      gameController.nextMove();
    }

    result.add(gameController.getGameStatus());

    Assertions.assertTrue(result.stream().allMatch(gameState -> gameState == GameState.WHITE_WIN));
  }
}
