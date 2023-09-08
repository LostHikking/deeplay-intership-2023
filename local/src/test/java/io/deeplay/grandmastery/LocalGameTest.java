package io.deeplay.grandmastery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.ui.ConsoleUi;
import java.io.IOException;
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
        Player player1 = new Randomus(Color.WHITE);
        Player player2 = new Randomus(Color.BLACK);
        GameController gameController = new GameController(player1, player2);
        gameController.beginPlay(i < COUNT_GAME / 2 ? ChessType.CLASSIC : ChessType.FISHERS);

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

  @Test
  public void createGameControllerBotVsBotTest() throws IOException {
    UI mockUI = mock(UI.class);
    when(mockUI.selectMode()).thenReturn(GameMode.BOT_VS_BOT);
    when(mockUI.selectBot(any(), any())).thenReturn("Randomus");

    Grandmastery.ui = mockUI;
    GameController gameController = Grandmastery.createGameController();
    Assertions.assertAll(
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void createGameControllerWhiteHumanVsBotTest() throws IOException {
    UI mockUI = mock(UI.class);
    when(mockUI.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(mockUI.selectColor()).thenReturn(Color.WHITE);
    when(mockUI.inputPlayerName(any())).thenReturn("TestPlayer");
    when(mockUI.selectBot(any(), any())).thenReturn("Randomus");

    Grandmastery.ui = mockUI;
    GameController gameController = Grandmastery.createGameController();
    Assertions.assertAll(
        () -> assertEquals("TestPlayer", gameController.getWhite().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void createGameControllerBlackHumanVsBotTest() throws IOException {
    UI mockUI = mock(UI.class);
    when(mockUI.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(mockUI.selectColor()).thenReturn(Color.BLACK);
    when(mockUI.inputPlayerName(any())).thenReturn("TestPlayer");
    when(mockUI.selectBot(any(), any())).thenReturn("Randomus");

    Grandmastery.ui = mockUI;
    GameController gameController = Grandmastery.createGameController();
    Assertions.assertAll(
        () -> assertEquals("TestPlayer", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void createGameControllerHumanVsHumanTest() throws IOException {
    UI mockUI = mock(UI.class);
    when(mockUI.selectMode()).thenReturn(GameMode.HUMAN_VS_HUMAN);
    when(mockUI.inputPlayerName(Color.WHITE)).thenReturn("TestWhitePlayer");
    when(mockUI.inputPlayerName(Color.BLACK)).thenReturn("TestBlackPlayer");

    Grandmastery.ui = mockUI;
    GameController gameController = Grandmastery.createGameController();
    Assertions.assertAll(
        () -> assertEquals("TestWhitePlayer", gameController.getWhite().getName()),
        () -> assertEquals("TestBlackPlayer", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void createUiTest() {
    Grandmastery.createUi("tui");
    assertTrue(Grandmastery.ui instanceof ConsoleUi);
  }

  @Test
  public void tryCreateUnknownUiTest() {
    assertThrows(IllegalArgumentException.class, () -> Grandmastery.createUi("ababa"));
  }
}
