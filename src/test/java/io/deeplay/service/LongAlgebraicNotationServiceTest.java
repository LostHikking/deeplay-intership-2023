package io.deeplay.service;

import io.deeplay.domain.FigureType;
import io.deeplay.exceptions.GameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongAlgebraicNotationServiceTest {

    @Test
    void getMoveFromString() {
        var move = LongAlgebraicNotationService.getMoveFromString("e2d4");

        assertEquals(5, move.from().col());
        assertEquals(2, move.from().row());
        assertEquals(4, move.to().col());
        assertEquals(4, move.to().row());
        assertNull(move.promotionPiece());

        move = LongAlgebraicNotationService.getMoveFromString("e2d4q");

        assertNotNull(move.promotionPiece());
        assertEquals(FigureType.QUEEN, move.promotionPiece());
    }

    @Test
    void getWrongMoveFromString() {
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMoveFromString("e2d4z"));
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMoveFromString("e2d9"));
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMoveFromString("e2z4b"));
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMoveFromString("12d4"));
    }

    @Test
    void getMovesFromString() {
        var moves = LongAlgebraicNotationService.getMovesFromString("e2e4,e7e5b");
        assertEquals(2, moves.size());

        var move = moves.get(0);
        assertEquals(5, move.from().col());
        assertEquals(2, move.from().row());
        assertEquals(5, move.to().col());
        assertEquals(4, move.to().row());

        assertNull(move.promotionPiece());
        assertNotNull(moves.get(1).promotionPiece());
    }

    @Test
    void getWrongMovesFromString() {
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMovesFromString("e2e4,e7e5b,a"));
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMovesFromString("e2e4,e9e5b"));
        assertThrows(GameException.class,
                () -> LongAlgebraicNotationService.getMovesFromString(""));
    }
}