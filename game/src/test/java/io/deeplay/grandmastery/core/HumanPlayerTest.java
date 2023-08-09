package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HumanPlayerTest {
  private final Board board = new HashBoard();
  private final UI testUi = mock(UI.class);
  private Player testPlayer;

  @BeforeEach
  public void initUI() {
    testPlayer = new HumanPlayer("TestPlayer", board, Color.WHITE, testUi);
  }

  @Test
  public void makeMoveValidInputTest() throws GameException, IOException {
    String testInput = "e2e4";
    String expectedMoveData = "e2e4";

    when(testUi.inputMove(anyString())).thenReturn(testInput);
    testPlayer.makeMove(board);
    assertEquals(expectedMoveData, LongAlgebraicNotation.moveToString(testPlayer.getMoveData()));
  }

  @Test
  public void invalidInputTest() throws IOException {
    String testInput = "e2k4\n";

    when(testUi.inputMove(anyString())).thenReturn(testInput);
    assertThrows(GameException.class, () -> testPlayer.makeMove(board));
  }

  @Test
  public void throwExceptionTest() throws IOException {
    when(testUi.inputMove(anyString())).thenThrow(IOException.class);
    assertThrows(RuntimeException.class, () -> testPlayer.makeMove(board));
  }
}
