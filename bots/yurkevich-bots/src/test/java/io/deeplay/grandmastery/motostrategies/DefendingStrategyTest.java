package io.deeplay.grandmastery.motostrategies;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import org.junit.jupiter.api.Test;

class DefendingStrategyTest {
  private DefendingStrategy defendingStrategy1;
  private DefendingStrategy defendingStrategy2;
  private State testState1;
  private State testState2;

  @Test
  void piecesSafetyTest() {
    defendingStrategy1 = new DefendingStrategy();
    defendingStrategy2 = new DefendingStrategy();

    Board board1 = new HashBoard();
    Queen whiteQueen1 = new Queen(Color.WHITE);
    Rook blackRook1 = new Rook(Color.BLACK);
    King blackKing1 = new King(Color.BLACK);
    King whiteKing1 = new King(Color.WHITE);
    board1.setPiece(new Position(new Column(0), new Row(2)), whiteQueen1);
    board1.setPiece(new Position(new Column(0), new Row(5)), blackRook1);
    board1.setPiece(new Position(new Column(7), new Row(7)), whiteKing1);
    board1.setPiece(new Position(new Column(7), new Row(0)), blackKing1);
    testState1 = new State(board1, Color.BLACK, Color.BLACK, null, null, true);
    defendingStrategy1.init(testState1);
    defendingStrategy1.setBasePiecesCost(Color.BLACK);
    defendingStrategy1.setBasePiecesCost(Color.WHITE);
    defendingStrategy1.piecesSafety(Color.BLACK);
    defendingStrategy1.setFinalStateCost();

    Queen whiteQueen2 = new Queen(Color.WHITE);
    Rook blackRook2 = new Rook(Color.BLACK);
    King blackKing2 = new King(Color.BLACK);
    King whiteKing2 = new King(Color.WHITE);
    Board board2 = new HashBoard();
    board2.setPiece(new Position(new Column(1), new Row(5)), blackRook2);
    board2.setPiece(new Position(new Column(0), new Row(7)), blackKing2);
    board2.setPiece(new Position(new Column(7), new Row(7)), whiteKing2);
    board2.setPiece(new Position(new Column(0), new Row(2)), whiteQueen2);
    testState2 = new State(board1, Color.BLACK, Color.BLACK, null, null, true);
    defendingStrategy2.init(testState2);
    defendingStrategy2.setBasePiecesCost(Color.BLACK);
    defendingStrategy2.setBasePiecesCost(Color.WHITE);
    defendingStrategy2.piecesSafety(Color.WHITE);
    defendingStrategy2.setFinalStateCost();

    int value1 = defendingStrategy1.getValue();
    int value2 = defendingStrategy2.getValue();
    assertTrue(value2 > value1);
  }

  @Test
  void mateForMainCostCheck() {
    defendingStrategy1 = new DefendingStrategy();

    Board testBoard = new HashBoard();
    testBoard.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(2)), new King(Color.BLACK));
    testState1 = new State(testBoard, Color.BLACK, Color.BLACK, null, null, true);
    defendingStrategy1.setTerminalCost(testState1);
    assertTrue(testState1.getValue() < 90000);
  }

  @Test
  void mateForOpponentCostCheck() {
    defendingStrategy1 = new DefendingStrategy();

    Board testBoard = new HashBoard();
    testBoard.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoard.setPiece(new Position(new Column(0), new Row(2)), new King(Color.BLACK));
    testState1 = new State(testBoard, Color.WHITE, Color.WHITE, null, null, true);
    defendingStrategy1.setTerminalCost(testState1);
    assertTrue(testState1.getValue() > 90000);
  }
}
