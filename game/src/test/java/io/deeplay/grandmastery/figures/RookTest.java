package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RookTest {
  private Piece piece;
  private Board board;

  @BeforeEach
  void init() {
    piece = new Rook(Color.WHITE);
    board = new HashBoard();
    board.setPiece(Position.getPositionFromString("f5"), piece);
    board.setPiece(Position.getPositionFromString("f3"), new Bishop(Color.BLACK));
    board.setPiece(Position.getPositionFromString("f7"), new King(Color.BLACK));
  }

  @ParameterizedTest
  @CsvSource(
      value = {"f5f6", "f5f4", "f5f3", "f5a5", "f5b5", "f5c5", "f5d5", "f5e5", "f5g5", "f5h5"})
  void moveCorrectTest(String moveStr) {
    var move = LongAlgebraicNotation.getMoveFromString(moveStr);

    Assertions.assertAll(
        () -> Assertions.assertTrue(piece.move(board, move)),
        () -> Assertions.assertNull(board.getPiece(move.from())),
        () -> Assertions.assertSame(piece, board.getPiece(move.to())));
  }

  @ParameterizedTest
  @CsvSource(
      value = {"f5a6", "f5f7", "f5f2", "f5f1", "f5f8", "f5c6", "f5a1", "f5h8", "f5g4", "f5h1"})
  void moveWrongTest(String moveStr) {
    var move = LongAlgebraicNotation.getMoveFromString(moveStr);

    Assertions.assertAll(
        () -> Assertions.assertFalse(piece.move(board, move)),
        () -> Assertions.assertNotNull(board.getPiece(move.from())),
        () -> Assertions.assertNotSame(piece, board.getPiece(move.to())));
  }

  @ParameterizedTest
  @CsvSource(
      value = {"f5f6", "f5f4", "f5f3", "f5a5", "f5b5", "f5c5", "f5d5", "f5e5", "f5g5", "f5h5"})
  void canMoveCorrectTest(String moveStr) {
    Assertions.assertTrue(
        piece.canMove(board, LongAlgebraicNotation.getMoveFromString(moveStr), true));
  }

  @ParameterizedTest
  @CsvSource(
      value = {"f5f5", "f5f7", "f5f2", "f5f1", "f5f8", "f5c6", "f5a1", "f5h8", "f5g4", "f5h1"})
  void canMoveWrongTest(String moveStr) {
    Assertions.assertFalse(
        piece.canMove(board, LongAlgebraicNotation.getMoveFromString(moveStr), true));
  }

  @Test
  void getAllMovesTest() {
    Assertions.assertEquals(
        10, piece.getAllMoves(board, Position.getPositionFromString("f5")).size());
  }

  @Test
  void canReviveTest() {
    Assertions.assertFalse(piece.canRevive(board, null));
  }
}
