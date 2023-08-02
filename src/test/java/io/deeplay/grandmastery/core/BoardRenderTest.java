package io.deeplay.grandmastery.core;

import static io.deeplay.grandmastery.core.BoardRender.showBoard;
import static io.deeplay.grandmastery.utils.BoardUtils.defaultChess;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardRenderTest {
  private InputStream originalIn;
  private ByteArrayInputStream testIn;
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
    Consumer<Board> defaultChess = defaultChess();
    defaultChess.accept(b);
    showBoard(System.out, b);

    String expectedOutput =
        "+-----------------------+\n"
            + "8 |♖|♘|♗|♕|♔|♗|♘|♖|\n"
            + "  +-----------------------+\n"
            + "7 |♙|♙|♙|♙|♙|♙|♙|♙|\n"
            + "  +-----------------------+\n"
            + "6 |  |  |  |  |  |  |  |  |\n"
            + "  +-----------------------+\n"
            + "5 |  |  |  |  |  |  |  |  |\n"
            + "  +-----------------------+\n"
            + "4 |  |  |  |  |  |  |  |  |\n"
            + "  +-----------------------+\n"
            + "3 |  |  |  |  |  |  |  |  |\n"
            + "  +-----------------------+\n"
            + "2 |♟|♟|♟|♟|♟|♟|♟|♟|\n"
            + "  +-----------------------+\n"
            + "1 |♜|♞|♝|♛|♚|♝|♞|♜|\n"
            + "  +-----------------------+\n"
            + "   a  b  c  d  e  f  g  h";

    assertEquals(expectedOutput, outContent.toString().trim());
  }
}
