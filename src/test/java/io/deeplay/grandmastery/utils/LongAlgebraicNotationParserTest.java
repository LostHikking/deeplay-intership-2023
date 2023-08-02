package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LongAlgebraicNotationParserTest {
  private static Board board;

  @BeforeAll
  static void init() {
    board = new HashBoard();
    BoardUtils.defaultChess().accept(board);
  }

  @Test
  void getSimpleMoveFromStringTest() {
    var move = LongAlgebraicNotationParser.getMoveFromString("e2d4");

    Assertions.assertAll(
        () -> Assertions.assertEquals(4, move.from().col().value()),
        () -> Assertions.assertEquals(1, move.from().row().value()),
        () -> Assertions.assertEquals(3, move.to().col().value()),
        () -> Assertions.assertEquals(3, move.to().row().value()),
        () -> Assertions.assertNull(move.promotionPiece()));
  }

  @Test
  void getPromotionMoveFromStringTest() {
    var move = LongAlgebraicNotationParser.getMoveFromString("e2d4q");

    Assertions.assertAll(
        () -> Assertions.assertNotNull(move.promotionPiece()),
        () -> Assertions.assertEquals(FigureType.QUEEN, move.promotionPiece()));
  }

  @Test
  void getWrongMoveFromStringTest() {
    Assertions.assertAll(
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("e2d4z")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("e2d9")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("e2z4b")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("12d4")));
  }

  @Test
  void getMovesFromStringTest() {
    var moves = LongAlgebraicNotationParser.getMovesFromString("e2e4,e7e5b");
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
                () -> LongAlgebraicNotationParser.getMovesFromString("e2e4,e7e5b,a")),
        () ->
            Assertions.assertThrows(
                GameException.class,
                () -> LongAlgebraicNotationParser.getMovesFromString("e2e4,e9e5b")),
        () ->
            Assertions.assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMovesFromString("")));
  }

  @Test
  void validMovesTest() {
    var stringMoves = "d2d4,d7d5,c2c4";
    var moves = LongAlgebraicNotationParser.getMovesFromString(stringMoves);

    // TODO: Дополнить тест, когда будут реализованы ходы фигур
    Assertions.assertTrue(LongAlgebraicNotationParser.validMoves(moves, board));
  }
}
