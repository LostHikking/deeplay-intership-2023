package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Rook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameStateCheckerTest {
  private Board board;

  @BeforeEach
  void init() {
    board = new HashBoard();
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
        // () -> Assertions.assertFalse(GameStateChecker.isMate(board, Color.BLACK)),
        // TODO: раскомментировать после реализации шаблонного метода move
        () -> Assertions.assertTrue(GameStateChecker.isMate(board, Color.WHITE)));
  }

  @Test
  void isDrawTestWithKings() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    //    Assertions.assertTrue(GameStateChecker.isDraw(board, null));
  }

  @Test
  void isDrawTestWithKingsAndRook() {
    board.setPiece(Position.getPositionFromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.getPositionFromString("h5"), new Rook(Color.WHITE));
    board.setPiece(Position.getPositionFromString("g7"), new King(Color.BLACK));

    Assertions.assertFalse(GameStateChecker.isDraw(board, null));
  }
}
