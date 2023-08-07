package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashBoardTest {
  private Board board;

  @BeforeEach
  void init() {
    board = new HashBoard();
  }

  @Test
  public void setAndGetPieceTest() {
    Position position = new Position(new Column(1), new Row(2));
    Piece pawn = new Pawn(Color.WHITE);

    board.setPiece(position, pawn);
    Position searchPosition = new Position(new Column(1), new Row(2));
    Piece retrievedPiece = board.getPiece(searchPosition);

    assertSame(pawn, retrievedPiece);
  }

  @Test
  public void autoSetBlackAndWhiteKingsTest() {
    Position whiteKingPosition = new Position(new Column(1), new Row(2));
    Piece whiteKing = new King(Color.WHITE);
    board.setPiece(whiteKingPosition, whiteKing);

    Position blackKingPosition = new Position(new Column(1), new Row(7));
    Piece blackKing = new King(Color.BLACK);
    board.setPiece(blackKingPosition, blackKing);

    Position retrievedWhiteKingPosition = board.getWhiteKingPosition();
    Position retrievedBlackKingPosition = board.getBlackKingPosition();

    Assertions.assertAll(
        () -> assertEquals(whiteKingPosition, retrievedWhiteKingPosition, "White king check"),
        () -> assertEquals(blackKingPosition, retrievedBlackKingPosition, "Black king check"));
  }

  @Test
  public void getPieceByRowAndColTest() {
    Position position = new Position(new Column(1), new Row(2));
    Piece pawn = new Pawn(Color.WHITE);

    board.setPiece(position, pawn);
    Piece retrievedPiece = board.getPiece(1, 2);

    assertSame(pawn, retrievedPiece);
  }

  @Test
  public void removePieceTest() {
    Position position = new Position(new Column(1), new Row(4));
    Piece pawn = new Pawn(Color.WHITE);

    board.setPiece(position, pawn);
    board.removePiece(position);
    Piece retrievedPiece = board.getPiece(position);

    assertNull(retrievedPiece);
  }

  @Test
  public void hasPieceTrueTest() {
    Position position = new Position(new Column(1), new Row(4));
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(position, pawn);

    assertTrue(board.hasPiece(position));
  }

  @Test
  public void hasPieceFalseTest() {
    Position position = new Position(new Column(1), new Row(4));
    assertFalse(board.hasPiece(position));
  }

  @Test
  public void getAllWhitePiecePositionTest() {
    Set<Position> positions = new HashSet<>();
    positions.add(new Position(new Column(1), new Row(4)));
    positions.add(new Position(new Column(2), new Row(4)));
    positions.add(new Position(new Column(3), new Row(4)));

    positions.forEach(position -> board.setPiece(position, new Pawn(Color.WHITE)));
    board.setPiece(Position.getPositionFromString("a1"), new Pawn(Color.BLACK));

    assertEquals(positions, new HashSet<>(board.getAllPieceByColorPosition(Color.WHITE)));
  }

  @Test
  public void getAllBlackPiecePositionTest() {
    Set<Position> positions = new HashSet<>();
    positions.add(new Position(new Column(1), new Row(4)));
    positions.add(new Position(new Column(2), new Row(4)));
    positions.add(new Position(new Column(3), new Row(4)));

    positions.forEach(position -> board.setPiece(position, new Pawn(Color.BLACK)));
    board.setPiece(Position.getPositionFromString("a1"), new Pawn(Color.WHITE));

    assertEquals(positions, new HashSet<>(board.getAllPieceByColorPosition(Color.BLACK)));
  }

  @Test
  public void getAllPiecePositionTest() {
    Set<Position> positions = new HashSet<>();
    positions.add(new Position(new Column(1), new Row(4)));
    positions.add(new Position(new Column(2), new Row(4)));
    positions.add(new Position(new Column(3), new Row(4)));
    positions.add(new Position(new Column(0), new Row(0)));

    positions.forEach(position -> board.setPiece(position, new Pawn(Color.BLACK)));
    board.setPiece(Position.getPositionFromString("a1"), new Pawn(Color.WHITE));

    assertEquals(positions, new HashSet<>(board.getAllPiecePosition()));
  }
}
