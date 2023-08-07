package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Rook;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameStateCheckerTest {
  private Board board;
  private GameHistory gameHistory;

  @BeforeEach
  void init() {
    board = new HashBoard();
    gameHistory = new GameHistory();
  }

  @Test
  void isCheckTest() {
    board.setPiece(Position.getPositionFromString("f3"), new Rook(Color.BLACK));
    board.setPiece(Position.getPositionFromString("f1"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("a1"), new King(Color.BLACK));
    board.setPiece(Position.getPositionFromString("a2"), new Knight(Color.WHITE));

    Assertions.assertAll(
        () -> Assertions.assertTrue(GameStateChecker.isCheck(board, Color.WHITE)),
        () -> Assertions.assertFalse(GameStateChecker.isCheck(board, Color.BLACK)));
  }

  @Test
  void isMateTest() {
    board.setPiece(Position.getPositionFromString("a1"), new Rook(Color.BLACK));
    board.setPiece(Position.getPositionFromString("b1"), new Rook(Color.BLACK));
    board.setPiece(Position.getPositionFromString("a8"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("c1"), new Rook(Color.WHITE));
    board.setPiece(Position.getPositionFromString("h1"), new King(Color.BLACK));

    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isMate(board, Color.BLACK)),
        () -> Assertions.assertTrue(GameStateChecker.isMate(board, Color.WHITE)));
  }

  @Test
  void isDrawTestWithKings() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isDrawTestWithKingsAndBishop() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("e6"), new Bishop(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isDrawTestWithKingsAndKnight() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("e6"), new Knight(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isDrawTestWithKingsAndTwoKnight() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("e6"), new Knight(Color.WHITE));
    board.setPiece(Position.getPositionFromString("e7"), new Knight(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isNotDrawTestWithKingsAndRook() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("e6"), new Rook(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertFalse(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isNotDrawTestWithKingsAndPawn() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("e6"), new Pawn(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertFalse(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isStaleMateTest() {
    board.setPiece(Position.getPositionFromString("h5"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("h7"), new Pawn(Color.WHITE));
    board.setPiece(Position.getPositionFromString("h8"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("h5h6");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertAll(
        () -> Assertions.assertTrue(GameStateChecker.isStaleMate(board, Color.BLACK)),
        () -> Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory)));
  }

  @Test
  void isNotStaleMateTest() {
    board.setPiece(Position.getPositionFromString("h4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("h7"), new Pawn(Color.WHITE));
    board.setPiece(Position.getPositionFromString("h8"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("h4h5");
    new King(Color.WHITE).move(board, move);

    gameHistory.makeMove(move, board);
    board.setLastMove(move);

    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isStaleMate(board, Color.BLACK)),
        () -> Assertions.assertFalse(GameStateChecker.isDraw(board, gameHistory)));
  }
}
