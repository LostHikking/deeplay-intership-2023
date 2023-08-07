package io.deeplay.grandmastery.core;

import static io.deeplay.grandmastery.core.BoardRender.showBoard;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

class BoardRenderTest {
  @Test
  void testShowBoard() {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    Board b = new HashBoard();
    b.setPiece(new Position(new Column(0), new Row(0)), new Pawn(Color.WHITE));
    b.setPiece(new Position(new Column(7), new Row(7)), new King(Color.BLACK));

    showBoard(outContent, b);
    String expectedOutput =
        """
                    ┏━━━━━━━━━━━━━━━━━━━━━━┓
                    ┃ 8 │ │ │ │ │ │ │ │♔│  ┃
                    ┃ 7 │ │ │ │ │ │ │ │ │  ┃
                    ┃ 6 │ │ │ │ │ │ │ │ │  ┃
                    ┃ 5 │ │ │ │ │ │ │ │ │  ┃
                    ┃ 4 │ │ │ │ │ │ │ │ │  ┃
                    ┃ 3 │ │ │ │ │ │ │ │ │  ┃
                    ┃ 2 │ │ │ │ │ │ │ │ │  ┃
                    ┃ 1 │♟│ │ │ │ │ │ │ │  ┃
                    ┃    a b c d e f g h   ┃
                    ┗━━━━━━━━━━━━━━━━━━━━━━┛""";
    assertEquals(expectedOutput, outContent.toString(Charset.defaultCharset()).trim());
  }
}
