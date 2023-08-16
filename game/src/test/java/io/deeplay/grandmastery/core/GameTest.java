package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
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
        () -> assertEquals(Color.WHITE, game.getColorMove()),
        () -> assertFalse(game.isGameOver()));
  }

  @Test
  public void makeMoveTest() {
    Position from = Position.getPositionFromString("e2");
    Position to = Position.getPositionFromString("e4");
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(from, pawn);

    Move move = new Move(from, to, null);
    game.startup(board);
    game.makeMove(move);

    Assertions.assertAll(
        () -> assertEquals(pawn, game.getCopyBoard().getPiece(to)),
        () -> assertNull(game.getCopyBoard().getPiece(from)),
        () -> assertEquals(Color.BLACK, game.getColorMove()));
  }

  @Test
  public void startPositionEmptyTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    game.startup(board);

    assertThrows(GameException.class, () -> game.makeMove(move));
  }

  @Test
  public void moveImpossibleTest() {
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(Position.getPositionFromString("e2"), pawn);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e5");
    game.startup(board);

    assertThrows(GameException.class, () -> game.makeMove(move));
  }

  @Test
  public void ifGameOverTest() {
    game.startup(board);
    game.gameOver();

    Move move = LongAlgebraicNotation.getMoveFromString("e2e5");
    assertThrows(GameException.class, () -> game.makeMove(move));
  }
}
