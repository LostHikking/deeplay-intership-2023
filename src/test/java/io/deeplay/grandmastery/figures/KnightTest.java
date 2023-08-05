package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.LongAlgebraicNotationParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KnightTest {
  private Piece piece;
  private Board board;

  @BeforeEach
  void init() {
    piece = new Knight(Color.WHITE);
    board = new HashBoard();
    board.setPiece(Position.getPositionFromString("e5"), piece);
    board.setPiece(Position.getPositionFromString("d3"), new Bishop(Color.WHITE));
    board.setPiece(Position.getPositionFromString("d7"), new Bishop(Color.BLACK));
    board.setPiece(Position.getPositionFromString("f7"), new King(Color.BLACK));
  }

  @ParameterizedTest
  @CsvSource(value = {"e5d7", "e5g6", "e5c6", "e5c4", "e5g4", "e5f3"})
  void moveCorrectTest(String moveStr) {
    var move = LongAlgebraicNotationParser.getMoveFromString(moveStr);

    Assertions.assertAll(
        () -> Assertions.assertTrue(piece.move(board, move)),
        () -> Assertions.assertNull(board.getPiece(move.from())),
        () -> Assertions.assertSame(piece, board.getPiece(move.to())));
  }

  @ParameterizedTest
  @CsvSource(value = {"e5d3", "e5f7", "e5a1", "e5h8", "e5d5", "e5c7"})
  void moveWrongTest(String moveStr) {
    var move = LongAlgebraicNotationParser.getMoveFromString(moveStr);

    Assertions.assertAll(
        () -> Assertions.assertFalse(piece.move(board, move)),
        () -> Assertions.assertNotNull(board.getPiece(move.from())),
        () -> Assertions.assertNotSame(piece, board.getPiece(move.to())));
  }

  @ParameterizedTest
  @CsvSource(value = {"e5d7", "e5g6", "e5c6", "e5c4", "e5g4", "e5f3"})
  void canMoveCorrectTest(String moveStr) {
    Assertions.assertTrue(
        piece.canMove(board, LongAlgebraicNotationParser.getMoveFromString(moveStr), true));
  }

  @ParameterizedTest
  @CsvSource(value = {"e5d3", "e5f7", "e5a1", "e5h8", "e5d5", "e5c7"})
  void canMoveWrongTest(String moveStr) {
    Assertions.assertFalse(
        piece.canMove(board, LongAlgebraicNotationParser.getMoveFromString(moveStr), true));
  }

  @Test
  void getAllMovesTest() {
    Assertions.assertEquals(
        6, piece.getAllMoves(board, Position.getPositionFromString("e5")).size());
  }

  @Test
  void canReviveTest() {
    Assertions.assertFalse(piece.canRevive(board, null));
  }
}
