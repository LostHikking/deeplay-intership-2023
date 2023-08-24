package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
  private Game game;

  private Board board;

  /** Инициализация игры. */
  @BeforeEach
  public void init() {
    game = new Game();
    board = new HashBoard();
  }

  @Test
  public void startupTest() {
    game.startup(board);

    Assertions.assertAll(
        () -> assertNotNull(board),
        () -> assertEquals(GameState.WHITE_MOVE, game.getGameState()),
        () -> assertFalse(game.isGameOver()));
  }

  @Test
  public void makeMoveTest() {
    Position from = Position.fromString("e2");
    Position to = Position.fromString("e4");
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(from, pawn);

    Move move = new Move(from, to, null);
    game.startup(board);
    game.makeMove(move);

    Assertions.assertAll(
        () -> assertEquals(pawn, game.getCopyBoard().getPiece(to)),
        () -> assertNull(game.getCopyBoard().getPiece(from)),
        () -> assertEquals(GameState.BLACK_MOVE, game.getGameState()));
  }

  @Test
  public void startPositionEmptyTest() {
    Move move = new Move(Position.fromString("e2"), Position.fromString("e4"), null);
    game.startup(board);

    assertThrows(GameException.class, () -> game.makeMove(move));
  }

  @Test
  public void moveImpossibleTest() {
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(Position.fromString("e2"), pawn);
    Move move = new Move(Position.fromString("e2"), Position.fromString("e5"), null);
    game.startup(board);

    assertThrows(GameException.class, () -> game.makeMove(move));
  }

  @Test
  public void ifGameOverTest() {
    game.startup(board);
    game.gameOver(GameState.WHITE_WIN);

    Move move = new Move(Position.fromString("e2"), Position.fromString("e4"), null);
    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_WIN, game.getGameState()),
        () -> assertTrue(game.isGameOver()),
        () -> assertThrows(GameException.class, () -> game.makeMove(move)));
  }
}
