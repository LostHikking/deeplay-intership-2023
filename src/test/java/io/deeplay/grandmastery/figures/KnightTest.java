package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.BoardUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KnightTest {
  private static Piece piece;
  private static Board board;

  @BeforeAll
  static void init() {
    piece = new Knight(Color.WHITE);
    board = new HashBoard();
    BoardUtils.defaultChess().accept(board);
  }

  @Test
  void moveTest() {
    // TODO: Сделать после реализации шаблонного метода
  }

  @Test
  void canMoveTest() {
    Assertions.assertAll(
        () ->
            Assertions.assertTrue(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("g1"),
                        Position.getPositionFromString("f3"),
                        null))),
        () ->
            Assertions.assertTrue(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("g1"),
                        Position.getPositionFromString("h3"),
                        null))),
        () ->
            Assertions.assertFalse(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("g1"),
                        Position.getPositionFromString("a1"),
                        null))));
  }

  @Test
  void getAllMovesTest() {
    var allMoves = piece.getAllMoves(board, Position.getPositionFromString("g1"));

    var firstMove =
        new Move(Position.getPositionFromString("g1"), Position.getPositionFromString("f3"), null);
    var secondMove =
        new Move(Position.getPositionFromString("g1"), Position.getPositionFromString("h3"), null);

    Assertions.assertAll(
        () -> Assertions.assertEquals(2, allMoves.size()),
        () -> Assertions.assertTrue(allMoves.contains(firstMove)),
        () -> Assertions.assertTrue(allMoves.contains(secondMove)));
  }

  @Test
  void canReviveTest() {
    Assertions.assertFalse(piece.canRevive(board, null));
  }
}
