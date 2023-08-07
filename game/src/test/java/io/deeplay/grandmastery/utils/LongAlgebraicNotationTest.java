package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LongAlgebraicNotationTest {
  private Board board;

  @BeforeEach
  void init() {
    board = new HashBoard();
    Boards.defaultChess().accept(board);
  }

  @Test
  void getSimpleMoveFromStringTest() {
    var move = LongAlgebraicNotation.getMoveFromString("e2d4");

    Assertions.assertAll(
        () -> Assertions.assertEquals(4, move.from().col().value()),
        () -> Assertions.assertEquals(1, move.from().row().value()),
        () -> Assertions.assertEquals(3, move.to().col().value()),
        () -> Assertions.assertEquals(3, move.to().row().value()),
        () -> Assertions.assertNull(move.promotionPiece()));
  }

  @Test
  void getPromotionMoveFromStringTest() {
    var move = LongAlgebraicNotation.getMoveFromString("e2d4q");

    Assertions.assertAll(
        () -> Assertions.assertNotNull(move.promotionPiece()),
        () -> Assertions.assertEquals(FigureType.QUEEN, move.promotionPiece()));
  }

  @Test
  void getWrongMoveFromStringTest() {
    Assertions.assertAll(
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotation.getMoveFromString("e2d4z")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotation.getMoveFromString("e2d9")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotation.getMoveFromString("e2z4b")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotation.getMoveFromString("12d4")));
  }

  @Test
  void getMovesFromStringTest() {
    var moves = LongAlgebraicNotation.getMovesFromString("e2e4,e7e5b");
    var move = moves.get(0);

    Assertions.assertAll(
        () -> Assertions.assertEquals(2, moves.size()),
        () -> Assertions.assertEquals(4, move.from().col().value()),
        () -> Assertions.assertEquals(1, move.from().row().value()),
        () -> Assertions.assertEquals(4, move.to().col().value()),
        () -> Assertions.assertEquals(3, move.to().row().value()),
        () -> Assertions.assertNull(move.promotionPiece()),
        () -> Assertions.assertNotNull(moves.get(1).promotionPiece()));
  }

  @Test
  void getWrongMovesFromStringTest() {
    Assertions.assertAll(
        () ->
            Assertions.assertThrows(
                GameException.class,
                () -> LongAlgebraicNotation.getMovesFromString("e2e4,e7e5b,a")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotation.getMovesFromString("e2e4,e9e5b")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotation.getMovesFromString("")));
  }

  @Test
  void validMovesTest() {
    var movesStr = "d2d4,d7d5,c2c4,d5c4,e2e4,b8c6,g1f3,g8f6,e4e5,f6d7,f1e2,e7e6,e1g1";
    var moves = LongAlgebraicNotation.getMovesFromString(movesStr);

    Assertions.assertTrue(LongAlgebraicNotation.validMoves(moves, board));
  }

  @Test
  void toStringMoveTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("d2d4");
    Move movePromotion = LongAlgebraicNotation.getMoveFromString("e7e8q");

    Assertions.assertAll(
        () -> Assertions.assertEquals("d2d4", LongAlgebraicNotation.moveToString(move)),
        () -> Assertions.assertEquals("e7e8q", LongAlgebraicNotation.moveToString(movePromotion)));
  }

  @Test
  void validMovesWrongTest() {
    var movesStr = "d2d4,d7d5,c2c4,d5c4,e2e4,b8c6,g1f3,g8f6,e4e5,f6d7,f1e2,e7e5";
    var moves = LongAlgebraicNotation.getMovesFromString(movesStr);

    Assertions.assertFalse(LongAlgebraicNotation.validMoves(moves, board));
  }
}
