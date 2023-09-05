package io.deeplay.grandmastery.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Rook;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AlgorithmsTest {

  @Test
  void copyHistoryAndMoveTest() {
    Board defaultBoard = new HashBoard();
    Boards.defaultChess().accept(defaultBoard);

    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    defaultBoard.getPiece(Position.fromString("e2")).move(defaultBoard, move);
    defaultBoard.setLastMove(move);

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(defaultBoard);
    GameHistory copyHistory = Algorithms.copyHistoryAndMove(defaultBoard, gameHistory);

    Assertions.assertAll(
        () -> assertEquals(1, copyHistory.getMoves().size()),
        () -> assertTrue(gameHistory.getMoves().isEmpty()));
  }

  @Test
  void copyAndMoveTest() {
    Board defaultBoard = new HashBoard();
    Boards.defaultChess().accept(defaultBoard);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    Board copyBoard = Algorithms.copyAndMove(move, defaultBoard);

    Assertions.assertAll(
        () -> assertEquals(move, copyBoard.getLastMove()),
        () -> assertNull(defaultBoard.getLastMove()));
  }

  @Test
  void getPossibleMovesWhiteTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("e2"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e7"), new Pawn(Color.BLACK));
    board.setPiece(Position.fromString("e8"), new King(Color.BLACK));

    Set<Move> expectMoveList = new HashSet<>();
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e2e3"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e2e4"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e1f1"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e1f2"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e1d1"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e1d2"));

    assertEquals(expectMoveList, new HashSet<>(Algorithms.getPossibleMoves(board, Color.WHITE)));
  }

  @Test
  void getPossibleMovesBlackTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("e2"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e7"), new Pawn(Color.BLACK));
    board.setPiece(Position.fromString("e8"), new King(Color.BLACK));

    Set<Move> expectMoveList = new HashSet<>();
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e7e6"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e7e5"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e8f8"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e8f7"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e8d8"));
    expectMoveList.add(LongAlgebraicNotation.getMoveFromString("e8d7"));

    assertEquals(expectMoveList, new HashSet<>(Algorithms.getPossibleMoves(board, Color.BLACK)));
  }

  @Test
  void isGameOverWhiteTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("a1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("b1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("a8"), new King(Color.WHITE));
    board.setPiece(Position.fromString("c1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new King(Color.BLACK));

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertTrue(Algorithms.isGameOver(board, gameHistory));
  }

  @Test
  void isGameOverBlackTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a8"), new King(Color.BLACK));
    board.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("h1"), new King(Color.WHITE));

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertTrue(Algorithms.isGameOver(board, gameHistory));
  }

  @Test
  void isGameOverDrawTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    Move move = LongAlgebraicNotation.getMoveFromString("e3e4");

    GameHistory gameHistory = new GameHistory();
    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    assertTrue(Algorithms.isGameOver(board, gameHistory));
  }

  @Test
  void evaluationBoardOpponentMateTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a8"), new King(Color.BLACK));
    board.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("h1"), new King(Color.WHITE));

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertEquals(1, Algorithms.evaluationBoard(board, gameHistory, Color.WHITE));
  }

  @Test
  void evaluationBoardMateUsTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("a1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("b1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("a8"), new King(Color.WHITE));
    board.setPiece(Position.fromString("c1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new King(Color.BLACK));

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertEquals(-1, Algorithms.evaluationBoard(board, gameHistory, Color.WHITE));
  }

  @Test
  void evaluationBoardDrawTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    Move move = LongAlgebraicNotation.getMoveFromString("e3e4");

    GameHistory gameHistory = new GameHistory();
    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    assertEquals(0, Algorithms.evaluationBoard(board, gameHistory, Color.WHITE));
  }

  @Test
  void inversColorWhiteTest() {
    assertEquals(Color.BLACK, Algorithms.inversColor(Color.WHITE));
  }

  @Test
  void inversColorBlackTest() {
    assertEquals(Color.WHITE, Algorithms.inversColor(Color.BLACK));
  }

  @Test
  void evaluationBoardTest() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertEquals(0.3311, Algorithms.evaluationBoard(board, gameHistory, Color.WHITE));
  }

  @Test
  void evaluationFuncWhiteTest() {
    Board defaultBoard = new HashBoard();
    Boards.defaultChess().accept(defaultBoard);

    assertEquals(0.3311, Algorithms.evaluationFunc(defaultBoard, Color.WHITE));
  }

  @Test
  void evaluationFuncBlackTest() {
    Board defaultBoard = new HashBoard();
    Boards.defaultChess().accept(defaultBoard);

    assertEquals(0.3311, Algorithms.evaluationFunc(defaultBoard, Color.BLACK));
  }

  @Test
  void countDigitTest() {
    assertEquals(4, Algorithms.countDigit(1234));
  }
}
