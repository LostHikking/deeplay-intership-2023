package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.InputListener;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
