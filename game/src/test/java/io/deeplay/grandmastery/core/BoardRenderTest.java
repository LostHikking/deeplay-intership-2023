package io.deeplay.grandmastery.core;

import static io.deeplay.grandmastery.core.BoardRender.showBoard;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.Boards;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BoardRenderTest {
  private static final Board board = new HashBoard();

  @BeforeAll
  public static void initBoard() {
    Boards.defaultChess().accept(board);
  }

  @Test
  void testShowWhiteBoard() {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    showBoard(outContent, board, Color.WHITE);
    String expectedOutput =
        """
8 │♖│♘│♗│♕│♔│♗│♘│♖│
7 │♙│♙│♙│♙│♙│♙│♙│♙│
6 │ │ │ │ │ │ │ │ │
5 │ │ │ │ │ │ │ │ │
4 │ │ │ │ │ │ │ │ │
3 │ │ │ │ │ │ │ │ │
2 │♟│♟│♟│♟│♟│♟│♟│♟│
1 │♜│♞│♝│♛│♚│♝│♞│♜│
   a b c d e f g h""";
    expectedOutput = expectedOutput.replaceAll(" ", " ");
    expectedOutput += " ";
    assertEquals(expectedOutput, outContent.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  void testShowBlackBoard() {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    showBoard(outContent, board, Color.BLACK);
    String expectedOutput =
        """
                      │♜│♞│♝│♚│♛│♝│♞│♜│ 1
                      │♟│♟│♟│♟│♟│♟│♟│♟│ 2
                      │ │ │ │ │ │ │ │ │ 3
                      │ │ │ │ │ │ │ │ │ 4
                      │ │ │ │ │ │ │ │ │ 5
                      │ │ │ │ │ │ │ │ │ 6
                      │♙│♙│♙│♙│♙│♙│♙│♙│ 7
                      │♖│♘│♗│♔│♕│♗│♘│♖│ 8
                     \s\sh g f e d c b a""";
    expectedOutput = expectedOutput.replaceAll(" ", " ");
    expectedOutput += " ";
    assertEquals(expectedOutput, outContent.toString(StandardCharsets.UTF_8).trim());
  }
}
