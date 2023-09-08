package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
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
    board.setPiece(Position.fromString("f3"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("f1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("a1"), new King(Color.BLACK));
    board.setPiece(Position.fromString("a2"), new Knight(Color.WHITE));

    Assertions.assertAll(
        () -> Assertions.assertTrue(GameStateChecker.isCheck(board, Color.WHITE)),
        () -> Assertions.assertFalse(GameStateChecker.isCheck(board, Color.BLACK)));
  }

  @Test
  void isCheckAfterPromotionPawnToQueenTest() {
    Piece pawn = new Pawn(Color.WHITE);

    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e8"), new King(Color.BLACK));
    board.setPiece(Position.fromString("a7"), pawn);

    Move move = LongAlgebraicNotation.getMoveFromString("a7a8q");
    pawn.move(board, move);
    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isCheck(board, Color.WHITE), "White"),
        () -> Assertions.assertTrue(GameStateChecker.isCheck(board, Color.BLACK), "Black"));
  }

  @Test
  void isCheckAfterPromotionPawnToKnightTest() {
    Piece pawn = new Pawn(Color.WHITE);

    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("b6"), new King(Color.BLACK));
    board.setPiece(Position.fromString("a7"), pawn);

    Move move = LongAlgebraicNotation.getMoveFromString("a7a8n");
    pawn.move(board, move);
    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isCheck(board, Color.WHITE), "White"),
        () -> Assertions.assertTrue(GameStateChecker.isCheck(board, Color.BLACK), "Black"));
  }

  @Test
  void isMateTest() {
    Piece testRook = new Rook(Color.BLACK);
    board.setPiece(Position.fromString("a1"), testRook);
    board.setPiece(Position.fromString("b1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("a8"), new King(Color.WHITE));
    board.setPiece(Position.fromString("c1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new King(Color.BLACK));

    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isMate(board, Color.BLACK)),
        () -> Assertions.assertTrue(GameStateChecker.isMate(board, Color.WHITE)),
        () -> Assertions.assertFalse(board.getPiece(Position.fromString("a1")).isMoved()));
  }

  @Test
  void isDrawTestWithKings() {
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isDrawTestWithKingsAndBishop() {
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e6"), new Bishop(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isDrawTestWithKingsAndKnight() {
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e6"), new Knight(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isDrawTestWithKingsAndTwoKnight() {
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e6"), new Knight(Color.WHITE));
    board.setPiece(Position.fromString("e7"), new Knight(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isNotDrawTestWithKingsAndRook() {
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e6"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertFalse(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isNotDrawTestWithKingsAndPawn() {
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e6"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("e4e5");
    new King(Color.WHITE).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertFalse(GameStateChecker.isDraw(board, gameHistory));
  }

  @Test
  void isStaleMateTest() {
    board.setPiece(Position.fromString("h5"), new King(Color.WHITE));
    board.setPiece(Position.fromString("h7"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("h8"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("h5h6");
    board.getPiece(Position.fromString("h5")).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isMate(board, Color.BLACK)),
        () -> Assertions.assertTrue(GameStateChecker.isStaleMate(board, Color.BLACK)),
        () -> Assertions.assertTrue(GameStateChecker.isDraw(board, gameHistory)));
  }

  @Test
  void isNotStaleMateTest() {
    board.setPiece(Position.fromString("h4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("h7"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("h8"), new King(Color.BLACK));

    var move = LongAlgebraicNotation.getMoveFromString("h4h5");
    board.getPiece(Position.fromString("h4")).move(board, move);

    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    Assertions.assertAll(
        () -> Assertions.assertFalse(GameStateChecker.isStaleMate(board, Color.BLACK)),
        () -> Assertions.assertFalse(GameStateChecker.isDraw(board, gameHistory)));
  }
}
