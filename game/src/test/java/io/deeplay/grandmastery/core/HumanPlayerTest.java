package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.InputListener;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HumanPlayerTest {
  private final InputListener testInputListener = mock(InputListener.class);
  private Player testPlayer;

  @BeforeEach
  public void initUI() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, testInputListener);
  }

  @Test
  public void makeMoveValidInputTest() throws GameException, IOException {
    String testInput = "e2e4";
    String expectedMoveData = "e2e4";

    when(testInputListener.inputMove(anyString())).thenReturn(testInput);
    testPlayer.createMove();
    assertEquals(expectedMoveData, testPlayer.getLastMove());
  }

  @Test
  public void invalidInputTest() throws IOException {
    String testInput = "e2k4\n";

    when(testInputListener.inputMove(anyString())).thenReturn(testInput);
    assertThrows(GameException.class, testPlayer::createMove);
  }

  @Test
  public void throwExceptionTest() throws IOException {
    when(testInputListener.inputMove(anyString())).thenThrow(IOException.class);
    assertThrows(RuntimeException.class, testPlayer::createMove);
  }

  /**
   * Тестовый случай для проверки сдачи в игре.
   *
   * @param input Входная строка.
   * @throws IOException В случае ошибки ввода-вывода.
   */
  @ParameterizedTest
  @ValueSource(strings = {"sur", "surrender"})
  public void surrenderTest(String input) throws IOException {
    when(testInputListener.inputMove(anyString())).thenReturn(input);
    when(testInputListener.confirmSur()).thenReturn(true);

    assertEquals(MoveType.SURRENDER, testPlayer.createMove().moveType());
  }

  @Test
  public void noSurrenderTest() throws IOException {
    when(testInputListener.inputMove(anyString())).thenReturn("sur", "e2e4");
    when(testInputListener.confirmSur()).thenReturn(false);

    testPlayer.createMove();
    assertEquals("e2e4", testPlayer.getLastMove());
  }

  @Test
  public void offerDrawTest() throws IOException {
    when(testInputListener.inputMove(anyString())).thenReturn("draw");
    assertEquals(MoveType.DRAW_OFFER, testPlayer.createMove().moveType());
  }

  @Test
  public void acceptDrawTest() throws IOException {
    when(testInputListener.answerDraw()).thenReturn(true);
    assertTrue(testPlayer.answerDraw());
  }

  @Test
  public void refuseDrawTest() throws IOException {
    when(testInputListener.answerDraw()).thenReturn(false);
    assertFalse(testPlayer.answerDraw());
  }

  @Test
  public void ifGameOverTest() {
    testPlayer.gameOver();
    assertThrows(GameException.class, testPlayer::createMove);
  }

  @Test
  public void throwIoExceptionTest() throws IOException {
    when(testInputListener.answerDraw()).thenThrow(IOException.class);
    assertThrows(RuntimeException.class, testPlayer::answerDraw);
  }
}
