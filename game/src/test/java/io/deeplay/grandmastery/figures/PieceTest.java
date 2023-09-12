package io.deeplay.grandmastery.figures;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import org.junit.jupiter.api.Test;

public class PieceTest {
  @Test
  public void movesSaveTest() {
    Piece pawn = new Pawn(Color.WHITE);
    Board board = new HashBoard();
    Position position = Position.fromString("e2");

    board.setPiece(position, pawn);
    pawn.getAllMoves(board, position);

    assertNotNull(pawn.moves);
  }

  @Test
  public void clearMovesTest() {
    Piece pawn = new Pawn(Color.WHITE);
    Board board = new HashBoard();
    Position position = Position.fromString("e2");

    board.setPiece(position, pawn);
    pawn.getAllMoves(board, position);
    pawn.move(board, pawn.moves.get(0));

    assertNull(pawn.moves);
  }
}
