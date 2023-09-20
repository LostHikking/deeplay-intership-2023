package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import io.deeplay.grandmastery.utils.Boards;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BonusesTest {
  private static final int TEST_COUNT_MOVE = 15;

  private Board board;

  @BeforeEach
  public void init() {
    board = new HashBoard();
  }

  @Test
  public void notWhiteMiddlegameTest() {
    GameHistory mockGameHistory = Mockito.mock(GameHistory.class);
    Mockito.when(mockGameHistory.getMoves()).thenReturn(Collections.emptyList());
    Boards.defaultChess().accept(board);

    Assertions.assertEquals(0.0, Bonuses.middlegame(board, mockGameHistory, Color.WHITE));
  }

  @Test
  public void whiteMiddlegameTest() {
    GameHistory mockGameHistory = Mockito.mock(GameHistory.class);
    List<Move> mockList = Mockito.mock();
    Mockito.when(mockGameHistory.getMoves()).thenReturn(mockList);
    Mockito.when(mockList.size()).thenReturn(TEST_COUNT_MOVE);
    Boards.defaultChess().accept(board);

    Assertions.assertEquals(-6.0, Bonuses.middlegame(board, mockGameHistory, Color.WHITE));
  }

  @Test
  public void notBlackMiddlegameTest() {
    GameHistory mockGameHistory = Mockito.mock(GameHistory.class);
    Mockito.when(mockGameHistory.getMoves()).thenReturn(Collections.emptyList());
    Boards.defaultChess().accept(board);

    Assertions.assertEquals(0.0, Bonuses.middlegame(board, mockGameHistory, Color.BLACK));
  }

  @Test
  public void blackMiddlegameTest() {
    GameHistory mockGameHistory = Mockito.mock(GameHistory.class);
    List<Move> mockList = Mockito.mock();
    Mockito.when(mockGameHistory.getMoves()).thenReturn(mockList);
    Mockito.when(mockList.size()).thenReturn(TEST_COUNT_MOVE);
    Boards.defaultChess().accept(board);

    Assertions.assertEquals(-6.0, Bonuses.middlegame(board, mockGameHistory, Color.BLACK));
  }

  @Test
  public void openLinesBeginGameTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.0, Bonuses.openLines(board, Color.WHITE, 0));
  }

  @Test
  public void openLinesForRookTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.5, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void notOpenVerticalLinesForRookTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a6"), new Pawn(Color.WHITE));
    Assertions.assertEquals(0.25, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void notOpenHorizontalLinesForRookTest() {
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.5, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesCenterForRookTest() {
    board.setPiece(Position.fromString("e1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("d1"), new Rook(Color.WHITE));
    Assertions.assertEquals(1.0, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void notOpenLinesCenterForRookTest() {
    board.setPiece(Position.fromString("e1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("d1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("e5"), new Pawn(Color.BLACK));
    board.setPiece(Position.fromString("d5"), new Pawn(Color.WHITE));

    Assertions.assertEquals(0.0, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesLastHorizontalsForWhiteRookTest() {
    board.setPiece(Position.fromString("a7"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a8"), new Rook(Color.WHITE));
    Assertions.assertEquals(1.0, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesLastHorizontalsForBlackRookTest() {
    board.setPiece(Position.fromString("a2"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("a1"), new Rook(Color.BLACK));
    Assertions.assertEquals(1.0, Bonuses.openLines(board, Color.BLACK, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesForQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.WHITE));
    Assertions.assertEquals(0.5, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void notOpenVerticalLinesForQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("a6"), new Pawn(Color.WHITE));
    Assertions.assertEquals(0.25, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void notOpenHorizontalLinesForQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new Rook(Color.WHITE));
    Assertions.assertEquals(0.5, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesCenterForQueenTest() {
    board.setPiece(Position.fromString("e1"), new Queen(Color.WHITE));
    Assertions.assertEquals(0.75, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void notOpenLinesCenterForQueenTest() {
    board.setPiece(Position.fromString("e1"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("e5"), new Pawn(Color.BLACK));

    Assertions.assertEquals(0.25, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesLastHorizontalsForWhiteQueenTest() {
    board.setPiece(Position.fromString("a7"), new Queen(Color.WHITE));
    Assertions.assertEquals(0.75, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesLastHorizontalsForBlackQueenTest() {
    board.setPiece(Position.fromString("a1"), new Queen(Color.BLACK));
    Assertions.assertEquals(0.75, Bonuses.openLines(board, Color.BLACK, TEST_COUNT_MOVE));
  }

  @Test
  public void openLinesNoPieceTest() {
    board.setPiece(Position.fromString("a2"), new Pawn(Color.WHITE));
    Assertions.assertEquals(0.0, Bonuses.openLines(board, Color.WHITE, TEST_COUNT_MOVE));
  }
}
