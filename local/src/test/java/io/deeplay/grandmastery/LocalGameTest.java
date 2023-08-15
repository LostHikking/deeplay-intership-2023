package io.deeplay.grandmastery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalGameTest {
  private static final int COUNT_GAME = 1000;
  private static final int GAME_TIMEOUT_SECONDS = 15;

  private static int errorGameOver = 0;
  private static int incorrectMove = 0;

  @Test
  public void localGameBotVsBotTest() {
    for (int i = 0; i < COUNT_GAME; i++) {
      try {
        Player player1 = new AiPlayer(Color.WHITE);
        Player player2 = new AiPlayer(Color.BLACK);
        GameController gameController = new GameController(player1, player2);

        gameController.beginPlay(ChessType.CLASSIC);

        CompletableFuture<Void> future =
            CompletableFuture.runAsync(
                () -> {
                  while (!gameController.isGameOver()) {
                    try {
                      gameController.nextMove();
                    } catch (GameException e) {
                      incorrectMove++;
                    }
                  }
                });

        future.get(GAME_TIMEOUT_SECONDS, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        fail("Игра №" + i + " превышено ограничение времени в " + GAME_TIMEOUT_SECONDS + " cек.");
      } catch (Exception e) {
        errorGameOver++;
      }
    }

    Assertions.assertAll(
        () -> assertEquals(0, errorGameOver, "Game errors"),
        () -> assertEquals(0, incorrectMove, "Incorrect moves"));
  }
}
