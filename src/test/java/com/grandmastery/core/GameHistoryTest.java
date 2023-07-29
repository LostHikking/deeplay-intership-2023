package com.grandmastery.core;

import com.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameHistoryTest {
    private GameHistory gameHistory;

    @BeforeEach
    void init() {
        gameHistory = new GameHistory();
    }

    @Test
    void setBoard() {
        var newGameHistory = new GameHistory();
        gameHistory.setBoard(new Board() {
        });

        Assertions.assertAll(
                () -> assertNull(newGameHistory.getBoard()),
                () -> assertNotNull(gameHistory.getBoard())
        );
    }

    @Test
    void getLastMove() {
        var move = new Move(new Position(1, 1), new Position(2, 2), null);
        gameHistory.makeMove(move);

        Assertions.assertAll(
                () -> assertEquals(move, gameHistory.getLastMove()),
                () -> assertFalse(gameHistory.isEmpty())
        );
    }

    @Test
    void getLastMoveFromEmptyHistory() {
        Assertions.assertAll(
                () -> assertThrows(GameException.class,
                        () -> gameHistory.getLastMove()),
                () -> assertTrue(gameHistory.isEmpty())
        );
    }

    @Test
    void getMoves() {
        var move1 = new Move(new Position(1, 1), new Position(2, 2), null);
        var move2 = new Move(new Position(2, 2), new Position(3, 3), null);

        gameHistory.makeMove(move1);
        gameHistory.makeMove(move2);

        Assertions.assertAll(
                () -> assertEquals(move2, gameHistory.getLastMove()),
                () -> assertEquals(2, gameHistory.getMoves().size())
        );
    }
}