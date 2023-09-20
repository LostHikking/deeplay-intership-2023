package io.deeplay.grandmastery.motostrategies;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import org.junit.jupiter.api.Test;

class AttackingStrategyTest {
  private AttackingStrategy attackingStrategy1;
  private AttackingStrategy attackingStrategy2;
  private State testState1;
  private State testState2;

  @Test
  void setCenterControlValue() {
    attackingStrategy1 = new AttackingStrategy();
    attackingStrategy2 = new AttackingStrategy();

    Board board1 = new HashBoard();
    Queen queen1 = new Queen(Color.BLACK);
    queen1.setMoved(true);
    King blackKing1 = new King(Color.BLACK);
    King whiteKing1 = new King(Color.WHITE);
    board1.setPiece(new Position(new Column(0), new Row(0)), queen1);
    board1.setPiece(new Position(new Column(7), new Row(7)), whiteKing1);
    board1.setPiece(new Position(new Column(7), new Row(0)), blackKing1);
    Move move =
            new Move(
                    new Position(new Column(0), new Row(1)),
                    new Position(new Column(0), new Row(0)),
                    null,
                    null);
    testState1 = new State(board1, Color.BLACK, Color.BLACK, move, null, true);
    attackingStrategy1.init(testState1);
    attackingStrategy1.setBasePiecesCost(Color.BLACK);
    attackingStrategy1.setBasePiecesCost(Color.WHITE);
    attackingStrategy1.setCenterControlValue(Color.BLACK);
    attackingStrategy1.setFinalStateCost();

    Board board2 = new HashBoard();
    Queen queen2 = new Queen(Color.BLACK);
    queen2.setMoved(true);
    board2.setPiece(new Position(new Column(1), new Row(1)), queen2);
    board2.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    board2.setPiece(new Position(new Column(7), new Row(0)), new King(Color.BLACK));
    testState2 = new State(board2, Color.BLACK, Color.BLACK, null, null, true);
    attackingStrategy2.init(testState2);
    attackingStrategy2.setBasePiecesCost(Color.BLACK);
    attackingStrategy2.setBasePiecesCost(Color.WHITE);
    attackingStrategy2.setCenterControlValue(Color.BLACK);
    attackingStrategy2.setFinalStateCost();
    int value1 = attackingStrategy1.getValue();
    int value2 = attackingStrategy2.getValue();

    assertTrue(value1 < value2);
  }

  @Test
  void piecesSafetyTest() {
    attackingStrategy1 = new AttackingStrategy();
    attackingStrategy2 = new AttackingStrategy();

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
    attackingStrategy1.init(testState1);
    attackingStrategy1.setBasePiecesCost(Color.BLACK);
    attackingStrategy1.setBasePiecesCost(Color.WHITE);
    attackingStrategy1.piecesSafety(Color.BLACK);
    attackingStrategy1.setFinalStateCost();

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
    attackingStrategy2.init(testState1);
    attackingStrategy2.setBasePiecesCost(Color.BLACK);
    attackingStrategy2.setBasePiecesCost(Color.WHITE);
    attackingStrategy2.piecesSafety(Color.WHITE);
    attackingStrategy2.setFinalStateCost();
    int value1 = attackingStrategy1.getValue();
    int value2 = attackingStrategy2.getValue();
    assertTrue(value2 > value1);
  }

  @Test
  void mateForMainCostCheck() {
    attackingStrategy1 = new AttackingStrategy();

    Board testBoard = new HashBoard();
    testBoard.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(2)), new King(Color.BLACK));
    testState1 = new State(testBoard, Color.BLACK, Color.BLACK, null, null, true);
    attackingStrategy1.setTerminalCost(testState1);
    assertTrue(testState1.getValue() < 90000);
  }

  @Test
  void mateForOpponentCostCheck() {
    attackingStrategy1 = new AttackingStrategy();

    Board testBoard = new HashBoard();
    testBoard.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoard.setPiece(new Position(new Column(0), new Row(2)), new King(Color.BLACK));
    testState1 = new State(testBoard, Color.WHITE, Color.WHITE, null, null, true);
    attackingStrategy1.setTerminalCost(testState1);
    assertTrue(testState1.getValue() > 90000);
  }
}
