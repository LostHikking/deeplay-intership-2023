package io.deeplay.grandmastery.core;

import static io.deeplay.grandmastery.core.BoardRender.showBoard;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.Boards;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
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
┏━━━━━━━━━━━━━━━━━━━━━━┓
┃ 8 ┃♖┃♘┃♗┃♕┃♔┃♗┃♘┃♖┃  ┃
┃ 7 ┃♙┃♙┃♙┃♙┃♙┃♙┃♙┃♙┃  ┃
┃ 6 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃  ┃
┃ 5 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃  ┃
┃ 4 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃  ┃
┃ 3 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃  ┃
┃ 2 ┃♟┃♟┃♟┃♟┃♟┃♟┃♟┃♟┃  ┃
┃ 1 ┃♜┃♞┃♝┃♛┃♚┃♝┃♞┃♜┃  ┃
┃    a b c d e f g h   ┃
┗━━━━━━━━━━━━━━━━━━━━━━┛""";
    assertEquals(expectedOutput, outContent.toString(Charset.defaultCharset()).trim());
  }

  @Test
  void testShowBlackBoard() {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    showBoard(outContent, board, Color.BLACK);
    String expectedOutput =
        """
┏━━━━━━━━━━━━━━━━━━━━━━┓
┃  ┃♜┃♞┃♝┃♚┃♛┃♝┃♞┃♜┃ 1 ┃
┃  ┃♟┃♟┃♟┃♟┃♟┃♟┃♟┃♟┃ 2 ┃
┃  ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ 3 ┃
┃  ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ 4 ┃
┃  ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ 5 ┃
┃  ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ 6 ┃
┃  ┃♙┃♙┃♙┃♙┃♙┃♙┃♙┃♙┃ 7 ┃
┃  ┃♖┃♘┃♗┃♔┃♕┃♗┃♘┃♖┃ 8 ┃
┃   h g f e d c b a    ┃
┗━━━━━━━━━━━━━━━━━━━━━━┛""";
    assertEquals(expectedOutput, outContent.toString(Charset.defaultCharset()).trim());
  }
}
