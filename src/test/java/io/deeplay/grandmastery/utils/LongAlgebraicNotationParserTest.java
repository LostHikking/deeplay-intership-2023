package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongAlgebraicNotationParserTest {
    @Test
    void getSimpleMoveFromString() {
        var move = LongAlgebraicNotationParser.getMoveFromString("e2d4");

        Assertions.assertAll(
                () -> assertEquals(5, move.from().col()),
                () -> assertEquals(2, move.from().row()),
                () -> assertEquals(4, move.to().col()),
                () -> assertEquals(4, move.to().row()),
                () -> assertNull(move.promotionPiece())
        );
    }

    @Test
    void getPromotionMoveFromString() {
        var move = LongAlgebraicNotationParser.getMoveFromString("e2d4q");

        Assertions.assertAll(
                () -> assertNotNull(move.promotionPiece()),
                () -> Assertions.assertEquals(FigureType.QUEEN, move.promotionPiece())
        );
    }

    @Test
    void getWrongMoveFromString() {
        Assertions.assertAll(
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMoveFromString("e2d4z")),
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMoveFromString("e2d9")),
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMoveFromString("e2z4b")),
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMoveFromString("12d4"))
        );
    }

    @Test
    void getMovesFromString() {
        var moves = LongAlgebraicNotationParser.getMovesFromString("e2e4,e7e5b");
        var move = moves.get(0);

        Assertions.assertAll(
                () -> assertEquals(2, moves.size()),
                () -> assertEquals(5, move.from().col()),
                () -> assertEquals(2, move.from().row()),
                () -> assertEquals(5, move.to().col()),
                () -> assertEquals(4, move.to().row()),
                () -> assertNull(move.promotionPiece()),
                () -> assertNotNull(moves.get(1).promotionPiece())
        );
    }

    @Test
    void getWrongMovesFromString() {
        Assertions.assertAll(
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMovesFromString("e2e4,e7e5b,a")),
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMovesFromString("e2e4,e9e5b")),
                () -> assertThrows(GameException.class,
                        () -> LongAlgebraicNotationParser.getMovesFromString(""))
        );
    }

}