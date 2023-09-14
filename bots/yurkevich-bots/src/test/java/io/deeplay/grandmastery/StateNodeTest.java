package io.deeplay.grandmastery;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

class StateNodeTest {
  private StateNode testNode1;
  private StateNode testNode2;

  @Test
  void setCenterControlValue() {
    Board board1 = new HashBoard();
    board1.setPiece(new Position(new Column(0), new Row(4)), new Rook(Color.BLACK));
    board1.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    board1.setPiece(new Position(new Column(7), new Row(0)), new King(Color.BLACK));
    testNode1 = new StateNode(board1, Color.BLACK, Color.BLACK, null, null, true);
    testNode1.setBasePiecesCost(Color.BLACK);
    testNode1.setCenterControlValue();
    testNode1.setFinalNodeCost();
    Board board2 = new HashBoard();
    board2.setPiece(new Position(new Column(4), new Row(4)), new Queen(Color.BLACK));
    board2.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    board2.setPiece(new Position(new Column(7), new Row(0)), new King(Color.BLACK));
    testNode2 = new StateNode(board2, Color.BLACK, Color.BLACK, null, null, true);
    testNode2.setBasePiecesCost(Color.BLACK);
    testNode2.setCenterControlValue();
    testNode2.setFinalNodeCost();
    assertTrue(testNode1.getValue() < testNode2.getValue());
  }

  @Test
  void piecesSafetyTest() {
    Board board1 = new HashBoard();
    board1.setPiece(new Position(new Column(0), new Row(2)), new Queen(Color.WHITE));
    board1.setPiece(new Position(new Column(0), new Row(5)), new Rook(Color.BLACK));
    board1.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    board1.setPiece(new Position(new Column(7), new Row(0)), new King(Color.BLACK));
    testNode1 = new StateNode(board1, Color.BLACK, Color.BLACK, null, null, true);
    testNode1.setBasePiecesCost(Color.BLACK);
    testNode1.piecesSafety(Color.BLACK);
    testNode1.setFinalNodeCost();
    Board board2 = new HashBoard();
    board2.setPiece(new Position(new Column(0), new Row(5)), new Rook(Color.WHITE));
    board2.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    board2.setPiece(new Position(new Column(0), new Row(7)), new King(Color.BLACK));
    board2.setPiece(new Position(new Column(5), new Row(7)), new Queen(Color.BLACK));
    testNode2 = new StateNode(board2, Color.BLACK, Color.BLACK, null, null, true);
    testNode2.setBasePiecesCost(Color.BLACK);
    testNode2.piecesSafety(Color.BLACK);
    testNode2.setFinalNodeCost();

    assertTrue(testNode2.getValue() > testNode1.getValue());
  }

  @Test
  void setCheckCostTest() {
    Board testBoardWithoutSurround = new HashBoard();
    testBoardWithoutSurround.setPiece(
        new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoardWithoutSurround.setPiece(
        new Position(new Column(1), new Row(0)), new Rook(Color.WHITE));
    testBoardWithoutSurround.setPiece(
        new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoardWithoutSurround.setPiece(
        new Position(new Column(1), new Row(2)), new King(Color.BLACK));
    testNode1 = new StateNode(testBoardWithoutSurround, Color.BLACK, Color.BLACK, null, null, true);
    testNode1.setCheckCost(5);
    Board testBoardWithSurround = new HashBoard();
    testBoardWithSurround.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoardWithSurround.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoardWithSurround.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoardWithSurround.setPiece(new Position(new Column(1), new Row(2)), new King(Color.BLACK));
    testNode2 = new StateNode(testBoardWithSurround, Color.BLACK, Color.BLACK, null, null, true);
    testNode2.setCheckCost(5);
    assertTrue(testNode2.getValue() > testNode1.getValue());
  }

  @Test
  void mateForMainCostCheck() {
    Board testBoard = new HashBoard();
    testBoard.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(2)), new King(Color.BLACK));
    testNode1 = new StateNode(testBoard, Color.BLACK, Color.BLACK, null, null, true);
    assertTrue(testNode1.getValue() < 10000);
  }

  @Test
  void mateForOpponentCostCheck() {
    Board testBoard = new HashBoard();
    testBoard.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(1), new Row(0)), new Queen(Color.WHITE));
    testBoard.setPiece(new Position(new Column(7), new Row(7)), new King(Color.WHITE));
    testBoard.setPiece(new Position(new Column(0), new Row(2)), new King(Color.BLACK));
    testNode1 = new StateNode(testBoard, Color.WHITE, Color.WHITE, null, null, true);
    assertTrue(testNode1.getValue() > 10000);
  }

  @Test
  void setKingInDangerCost() {}
}
