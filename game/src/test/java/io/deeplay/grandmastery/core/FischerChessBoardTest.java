package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FischerChessBoardTest {
  private static Board board;

  @BeforeAll
  public static void init() {
    board = new HashBoard();
    Boards.fischerChess().accept(board);
  }

  @Test
  public void kingTest() {
    assertTrue(
        board.getBlackKingPosition().col().value() > 0
            && board.getBlackKingPosition().col().value() < 7
            && board.getWhiteKingPosition().col().value() > 0
            && board.getWhiteKingPosition().col().value() < 7);
  }

  @Test
  public void rookTest() {
    ArrayList<Position> rookPositions = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      if (board.getPiece(i, 0).getFigureType().getSymbol() == 'r'
          && board.getPiece(i, 7).getFigureType().getSymbol() == 'r') {
        rookPositions.add(new Position(new Column(i), new Row(0)));
        rookPositions.add(new Position(new Column(i), new Row(0)));
      }
    }
    assertAll(
        () -> assertTrue(rookPositions.size() == 4),
        () ->
            assertTrue(
                rookPositions.get(0).col().value() < board.getWhiteKingPosition().col().value()),
        () ->
            assertTrue(
                rookPositions.get(2).col().value() > board.getWhiteKingPosition().col().value()));
  }

  @Test
  public void bishopTest() {
    ArrayList<Position> bishopPositions = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      if (board.getPiece(i, 0).getFigureType().getSymbol() == 'b'
          && board.getPiece(i, 7).getFigureType().getSymbol() == 'b') {
        bishopPositions.add(new Position(new Column(i), new Row(0)));
        bishopPositions.add(new Position(new Column(i), new Row(0)));
      }
    }
    assertAll(
        () -> assertTrue(bishopPositions.size() == 4),
        () ->
            assertTrue(
                bishopPositions.get(0).col().value() % 2
                    != bishopPositions.get(2).col().value() % 2));
  }

  @Test
  public void knightTest() {
    int k = 0;
    for (int i = 0; i < 8; i++) {
      if (board.getPiece(i, 0).getFigureType().getSymbol() == 'n'
          && board.getPiece(i, 7).getFigureType().getSymbol() == 'n') {
        k++;
      }
    }
    assertTrue(k == 2);
  }

  @Test
  public void queenTest() {
    boolean flag = false;
    for (int i = 0; i < 8; i++) {
      if (board.getPiece(i, 0).getFigureType().getSymbol() == 'q'
          && board.getPiece(i, 7).getFigureType().getSymbol() == 'q') {
        flag = true;
      }
    }
    assertTrue(flag);
  }
}
