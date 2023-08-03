package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RookTest {
  private static Piece piece;
  private static Board board;

  @BeforeAll
  static void init() {
    piece = new Rook(Color.WHITE);
    board = new HashBoard();
    board.setPiece(Position.getPositionFromString("f5"), piece);
    board.setPiece(Position.getPositionFromString("f3"), new Bishop(Color.BLACK));
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
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("f8"),
                        null))),
        () ->
            Assertions.assertTrue(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("f3"),
                        null))),
        () ->
            Assertions.assertFalse(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("f1"),
                        null))),
        () ->
            Assertions.assertTrue(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("h5"),
                        null))),
        () ->
            Assertions.assertTrue(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("a5"),
                        null))),
        () ->
            Assertions.assertFalse(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("a1"),
                        null))),
        () ->
            Assertions.assertFalse(
                piece.canMove(
                    board,
                    new Move(
                        Position.getPositionFromString("f5"),
                        Position.getPositionFromString("h8"),
                        null))));
  }

  @Test
  void getAllMovesTest() {
    var startPos = Position.getPositionFromString("f5");
    var allMoves = piece.getAllMoves(board, startPos);

    Assertions.assertAll(
        () -> Assertions.assertEquals(12, allMoves.size()),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(0), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(1), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(2), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(3), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(4), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(6), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(7), new Row(4)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(5), new Row(7)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(5), new Row(6)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(5), new Row(5)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(5), new Row(3)), null))),
        () ->
            Assertions.assertTrue(
                allMoves.contains(
                    new Move(startPos, new Position(new Column(5), new Row(2)), null))));
  }

  @Test
  void canRevive() {
    Assertions.assertFalse(piece.canRevive(board, null));
  }
}
