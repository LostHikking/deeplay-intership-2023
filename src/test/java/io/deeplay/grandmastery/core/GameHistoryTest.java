package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameHistoryTest {
  private GameHistory gameHistory;

  @BeforeEach
  void init() {
    gameHistory = new GameHistory();
  }

  @Test
  void setBoardTest() {
    var newGameHistory = new GameHistory();
    gameHistory.setBoard(new Board() {});

    Assertions.assertAll(
        () -> assertNull(newGameHistory.getBoard()), () -> assertNotNull(gameHistory.getBoard()));
  }

  @Test
  void getLastMoveTest() {
    var move =
        new Move(
            new Position(new Column(1), new Row(1)), new Position(new Column(2), new Row(2)), null);
    gameHistory.makeMove(move);

    Assertions.assertAll(
        () -> assertEquals(move, gameHistory.getLastMove()),
        () -> assertFalse(gameHistory.isEmpty()));
  }

  @Test
  void getLastMoveFromEmptyHistoryTest() {
    Assertions.assertAll(
        () -> assertThrows(GameException.class, () -> gameHistory.getLastMove()),
        () -> assertTrue(gameHistory.isEmpty()));
  }

  @Test
  void getMovesTest() {
    var move1 =
        new Move(
            new Position(new Column(1), new Row(1)), new Position(new Column(2), new Row(2)), null);
    var move2 =
        new Move(
            new Position(new Column(2), new Row(2)), new Position(new Column(3), new Row(3)), null);

    gameHistory.makeMove(move1);
    gameHistory.makeMove(move2);

    Assertions.assertAll(
        () -> assertEquals(move2, gameHistory.getLastMove()),
        () -> assertEquals(2, gameHistory.getMoves().size()));
  }
}
