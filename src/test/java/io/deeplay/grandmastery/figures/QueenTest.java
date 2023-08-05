package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.LongAlgebraicNotationParser;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueenTest {
  private Board board = new HashBoard();

  @BeforeEach
  void setUp() {
    board = new HashBoard();
  }

  @Test
  void canMoveDiagonallyOnBlackFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(3));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveDiagonallyOnWhiteFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(3));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.WHITE));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveDiagonallyThroughBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(2), new Row(2));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveDiagonallyWithoutBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveHorizontallyOnBlackFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(0));
    Position p3 = new Position(new Column(3), new Row(0));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveHorizontallyOnWhiteFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(0));
    Position p3 = new Position(new Column(3), new Row(0));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.WHITE));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveHorizontallyThroughBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(2), new Row(0));
    Position p3 = new Position(new Column(3), new Row(0));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveHorizontallyWithoutBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(0));
    board.setPiece(p1, new Queen(Color.WHITE));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p2, FigureType.BISHOP), true));
  }

  @Test
  void canMoveVerticallyOnBlackFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(0), new Row(3));
    Position p3 = new Position(new Column(0), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveVerticallyOnWhiteFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(0), new Row(3));
    Position p3 = new Position(new Column(0), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.WHITE));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveVerticallyThroughBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(0), new Row(2));
    Position p3 = new Position(new Column(0), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveVerticallyWithoutBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p3 = new Position(new Column(0), new Row(3));
    board.setPiece(p1, new Queen(Color.WHITE));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void getAllMovesTestWithoutPieces() {
    Position p = new Position(new Column(3), new Row(3));
    Queen queen = new Queen(Color.BLACK);
    board.setPiece(p, queen);
    List<Move> moves = new ArrayList<>();
    moves.add(new Move(p, new Position(new Column(4), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(6)), null));
    moves.add(new Move(p, new Position(new Column(7), new Row(7)), null));

    moves.add(new Move(p, new Position(new Column(2), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(0)), null));

    moves.add(new Move(p, new Position(new Column(4), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(0)), null));

    moves.add(new Move(p, new Position(new Column(2), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(6)), null));

    moves.add(new Move(p, new Position(new Column(3), new Row(0)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(2), new Row(3)), null));

    moves.add(new Move(p, new Position(new Column(3), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(4), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(6)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(7)), null));
    moves.add(new Move(p, new Position(new Column(7), new Row(3)), null));

    Assertions.assertEquals(moves, queen.getAllMoves(board, p));
  }

  @Test
  void getAllMovesTestWithPieces1() {
    Position p = new Position(new Column(3), new Row(3));
    Queen queen = new Queen(Color.BLACK);
    board.setPiece(p, queen);
    board.setPiece(new Position(new Column(4), new Row(3)), new Bishop(Color.BLACK));
    board.setPiece(new Position(new Column(3), new Row(4)), new Bishop(Color.BLACK));

    List<Move> moves = new ArrayList<>();
    moves.add(new Move(p, new Position(new Column(4), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(6)), null));
    moves.add(new Move(p, new Position(new Column(7), new Row(7)), null));

    moves.add(new Move(p, new Position(new Column(2), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(0)), null));

    moves.add(new Move(p, new Position(new Column(4), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(0)), null));

    moves.add(new Move(p, new Position(new Column(2), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(6)), null));

    moves.add(new Move(p, new Position(new Column(3), new Row(0)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(3)), null));
    moves.add(new Move(p, new Position(new Column(3), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(2), new Row(3)), null));

    Assertions.assertEquals(moves, queen.getAllMoves(board, p));
  }

  @Test
  void wrongMoveQueenTest() {
    var hashBoard = new HashBoard();
    var queen = new Queen(Color.WHITE);

    hashBoard.setPiece(Position.getPositionFromString("e6"), queen);
    Assertions.assertFalse(
        queen.canMove(hashBoard, LongAlgebraicNotationParser.getMoveFromString("e6h8"), true));
  }

  @Test
  void getAllMovesTestWithPieces2() {
    var hashBoard = new HashBoard();
    var queen = new Queen(Color.WHITE);
    var pos = Position.getPositionFromString("e5");

    hashBoard.setPiece(pos, queen);
    hashBoard.setPiece(Position.getPositionFromString("f3"), new King(Color.WHITE));
    hashBoard.setPiece(Position.getPositionFromString("a7"), new King(Color.BLACK));
    hashBoard.setPiece(Position.getPositionFromString("a1"), new Rook(Color.BLACK));
    hashBoard.setPiece(Position.getPositionFromString("e1"), new Rook(Color.BLACK));
    hashBoard.setPiece(Position.getPositionFromString("h2"), new Rook(Color.BLACK));
    hashBoard.setPiece(Position.getPositionFromString("h8"), new Rook(Color.BLACK));
    hashBoard.setPiece(Position.getPositionFromString("e8"), new Rook(Color.BLACK));
    hashBoard.setPiece(Position.getPositionFromString("b8"), new Rook(Color.BLACK));
    Assertions.assertEquals(27, queen.getAllMoves(hashBoard, pos).size());
  }

}
