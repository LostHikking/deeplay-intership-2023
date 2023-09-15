package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BonusesTest {
  private Bonuses bonuses;
  private Board board;

  @BeforeEach
  public void init() {
    bonuses = new Bonuses();
    board = new HashBoard();
  }

  @Test
  public void openLinesForRookTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void notOpenVerticalLinesForRookTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a6"), new Pawn(Color.WHITE));
    Assertions.assertEquals(0.25, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void notOpenHorizontalLinesForRookTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void openLinesCenterForRookTest() {
    board.setPiece(Position.fromString("e1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("d1"), new Rook(Color.WHITE));
    Assertions.assertEquals(1.0, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void notOpenLinesCenterForRookTest() {
    board.setPiece(Position.fromString("e1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("d1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("e5"), new Pawn(Color.BLACK));
    board.setPiece(Position.fromString("d5"), new Pawn(Color.WHITE));

    Assertions.assertEquals(0.0, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void openLinesLastHorizontalsForWhiteRookTest() {
    board.setPiece(Position.fromString("a7"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a8"), new Rook(Color.WHITE));
    Assertions.assertEquals(1.0, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void openLinesLastHorizontalsForBlackRookTest() {
    board.setPiece(Position.fromString("a2"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("a1"), new Rook(Color.BLACK));
    Assertions.assertEquals(1.0, bonuses.openLines(board, Color.BLACK));
  }

  @Test
  public void openLinesForQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.WHITE));
    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void notOpenVerticalLinesForQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("a6"), new Pawn(Color.WHITE));
    Assertions.assertEquals(0.25, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void notOpenHorizontalLinesForQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void openLinesCenterForQueenTest() {
    board.setPiece(Position.fromString("e1"), new Queen(Color.WHITE));
    Assertions.assertEquals(0.75, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void notOpenLinesCenterForQueenTest() {
    board.setPiece(Position.fromString("e1"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("e5"), new Pawn(Color.BLACK));

    Assertions.assertEquals(0.25, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void openLinesLastHorizontalsForWhiteQueenTest() {
    board.setPiece(Position.fromString("a7"), new Queen(Color.WHITE));
    Assertions.assertEquals(0.75, bonuses.openLines(board, Color.WHITE));
  }

  @Test
  public void openLinesLastHorizontalsForBlackQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.BLACK));
    Assertions.assertEquals(0.75, bonuses.openLines(board, Color.BLACK));
  }

  //  @Test
  //  public void openLinesAllDiagonalForBishopTest() {
  //    board.setPiece(Position.fromString("c2"), new Bishop(Color.WHITE));
  //    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  //  }
  //
  //  @Test
  //  public void openLinesAllDiagonalWhiteCenterForBishopTest() {
  //    board.setPiece(Position.fromString("e4"), new Bishop(Color.WHITE));
  //    Assertions.assertEquals(0.75, bonuses.openLines(board, Color.WHITE));
  //  }
  //
  //  @Test
  //  public void openLinesDiagonalLeftCornerForBishopTest() {
  //    board.setPiece(Position.fromString("a1"), new Bishop(Color.WHITE));
  //    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  //  }
  //
  //  @Test
  //  public void openLinesDiagonalRightCornerForBishopTest() {
  //    board.setPiece(Position.fromString("a8"), new Bishop(Color.WHITE));
  //    Assertions.assertEquals(0.5, bonuses.openLines(board, Color.WHITE));
  //  }

  //  @Test
  //  public void openLinesAllDiagonalBlackCenterForBishopTest() {
  //    board.setPiece(Position.fromString("d4"), new Bishop(Color.WHITE));
  //    Assertions.assertEquals(0.75, bonuses.openLines(board, Color.WHITE));
  //  }

  @Test
  public void openLinesNoPieceTest() {
    board.setPiece(Position.fromString("a2"), new Pawn(Color.WHITE));
    Assertions.assertEquals(0.0, bonuses.openLines(board, Color.WHITE));
  }
}
