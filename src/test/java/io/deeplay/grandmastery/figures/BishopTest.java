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

class BishopTest {
  private Board board;

  @BeforeEach
  void setUp() {
    board = new HashBoard();
  }

  @Test
  void canMoveOnBlackFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(3));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Bishop(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveOnWhiteFigure() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(3));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Bishop(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.WHITE));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveThroughBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(2), new Row(2));
    Position p3 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Bishop(Color.WHITE));
    board.setPiece(p2, new Bishop(Color.BLACK));

    Assertions.assertFalse(
        board.getPiece(p1).canMove(board, new Move(p1, p3, FigureType.BISHOP), true));
  }

  @Test
  void canMoveWithoutBarrier() {
    Position p1 = new Position(new Column(0), new Row(0));
    Position p2 = new Position(new Column(3), new Row(3));
    board.setPiece(p1, new Bishop(Color.WHITE));

    Assertions.assertTrue(
        board.getPiece(p1).canMove(board, new Move(p1, p2, FigureType.BISHOP), true));
  }

  @Test
  void getAllMovesTestWithoutPieces() {
    Position p = new Position(new Column(3), new Row(3));
    Bishop bishop = new Bishop(Color.BLACK);
    board.setPiece(p, bishop);
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

    Assertions.assertEquals(moves, bishop.getAllMoves(board, p));
  }

  @Test
  void getAllMovesTestWithPieces() {
    Position p = new Position(new Column(3), new Row(3));
    Bishop bishop = new Bishop(Color.BLACK);
    Queen queen = new Queen(Color.BLACK);
    board.setPiece(p, bishop);
    board.setPiece(new Position(new Column(2), new Row(2)), queen);
    List<Move> moves = new ArrayList<>();

    moves.add(new Move(p, new Position(new Column(4), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(6)), null));
    moves.add(new Move(p, new Position(new Column(7), new Row(7)), null));

    moves.add(new Move(p, new Position(new Column(4), new Row(2)), null));
    moves.add(new Move(p, new Position(new Column(5), new Row(1)), null));
    moves.add(new Move(p, new Position(new Column(6), new Row(0)), null));

    moves.add(new Move(p, new Position(new Column(2), new Row(4)), null));
    moves.add(new Move(p, new Position(new Column(1), new Row(5)), null));
    moves.add(new Move(p, new Position(new Column(0), new Row(6)), null));

    Assertions.assertEquals(moves.size(), bishop.getAllMoves(board, p).size());
  }

  @Test
  void wrongMoveBishopTest() {
    var hashBoard = new HashBoard();
    var bishop = new Bishop(Color.WHITE);

    hashBoard.setPiece(Position.getPositionFromString("e6"), bishop);
    Assertions.assertFalse(
        bishop.canMove(hashBoard, LongAlgebraicNotationParser.getMoveFromString("e6h8"), true));
  }
}
