package io.deeplay.grandmastery.core;

import static io.deeplay.grandmastery.core.BoardRender.showBoard;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardRenderTest {
  private InputStream originalIn;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void init() {
    originalIn = System.in;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void cleanup() {
    System.setIn(originalIn);
    System.setOut(System.out);
  }

  @Test
  void testShowBoard() {
    Board b = new HashBoard();
    b.setPiece(new Position(new Column(0), new Row(0)), new Pawn(Color.WHITE));
    b.setPiece(new Position(new Column(7), new Row(7)), new King(Color.BLACK));

    showBoard(System.out, b);

    String expectedOutput =
        """
| | | | | | | |♔|
| | | | | | | | |
| | | | | | | | |
| | | | | | | | |
| | | | | | | | |
| | | | | | | | |
| | | | | | | | |
|♟| | | | | | | |""";

    assertEquals(expectedOutput, outContent.toString(StandardCharsets.UTF_8).trim());
  }
}
