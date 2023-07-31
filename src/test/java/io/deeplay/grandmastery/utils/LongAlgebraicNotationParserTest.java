package io.deeplay.grandmastery.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LongAlgebraicNotationParserTest {
  @Test
  void getSimpleMoveFromStringTest() {
    var move = LongAlgebraicNotationParser.getMoveFromString("e2d4");

    Assertions.assertAll(
        () -> assertEquals(5, move.from().col().value()),
        () -> assertEquals(2, move.from().row().value()),
        () -> assertEquals(4, move.to().col().value()),
        () -> assertEquals(4, move.to().row().value()),
        () -> assertNull(move.promotionPiece()));
  }

  @Test
  void getPromotionMoveFromStringTest() {
    var move = LongAlgebraicNotationParser.getMoveFromString("e2d4q");

    Assertions.assertAll(
        () -> assertNotNull(move.promotionPiece()),
        () -> Assertions.assertEquals(FigureType.QUEEN, move.promotionPiece()));
  }

  @Test
  void getWrongMoveFromStringTest() {
    Assertions.assertAll(
        () ->
            assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("e2d4z")),
        () ->
            assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("e2d9")),
        () ->
            assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("e2z4b")),
        () ->
            assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMoveFromString("12d4")));
  }

  @Test
  void getMovesFromStringTest() {
    var moves = LongAlgebraicNotationParser.getMovesFromString("e2e4,e7e5b");
    var move = moves.get(0);

    Assertions.assertAll(
        () -> assertEquals(2, moves.size()),
        () -> assertEquals(5, move.from().col().value()),
        () -> assertEquals(2, move.from().row().value()),
        () -> assertEquals(5, move.to().col().value()),
        () -> assertEquals(4, move.to().row().value()),
        () -> assertNull(move.promotionPiece()),
        () -> assertNotNull(moves.get(1).promotionPiece()));
  }

  @Test
  void getWrongMovesFromStringTest() {
    Assertions.assertAll(
        () ->
            assertThrows(
                GameException.class,
                () -> LongAlgebraicNotationParser.getMovesFromString("e2e4,e7e5b,a")),
        () ->
            assertThrows(
                GameException.class,
                () -> LongAlgebraicNotationParser.getMovesFromString("e2e4,e9e5b")),
        () ->
            assertThrows(
                GameException.class, () -> LongAlgebraicNotationParser.getMovesFromString("")));
  }
}
