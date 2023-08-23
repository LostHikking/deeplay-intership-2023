package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiPlayerTest {
  private HashBoard board;
  private AiPlayer aiPlayer;

  @BeforeEach
  void init() {
    board = new HashBoard();
    aiPlayer = new AiPlayer(Color.WHITE);
  }

  @Test
  void makeMoveWithPawn() {
    board.setPiece(new Position(new Column(0), new Row(0)), new Pawn(Color.WHITE));
    aiPlayer.startup(board);
    aiPlayer.createMove();
    ArrayList<Move> moves = new ArrayList<>();
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(0), new Row(1)),
            null));
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(0), new Row(2)),
            null));
    assertTrue(moves.contains(aiPlayer.getLastMove()));
  }

  @Test
  void makeMoveWithKnight() {
    board.setPiece(new Position(new Column(0), new Row(0)), new Knight(Color.WHITE));
    aiPlayer.startup(board);
    aiPlayer.createMove();
    ArrayList<Move> moves = new ArrayList<>();
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(1), new Row(2)),
            null));
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(2), new Row(1)),
            null));
    assertTrue(moves.contains(aiPlayer.getLastMove()));
  }

  @Test
  void makeMoveWithRook() {
    board.setPiece(new Position(new Column(0), new Row(0)), new Rook(Color.WHITE));
    aiPlayer.startup(board);
    aiPlayer.createMove();
    ArrayList<Move> moves = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      moves.add(
          new Move(
              new Position(new Column(0), new Row(0)),
              new Position(new Column(i), new Row(0)),
              null));
      moves.add(
          new Move(
              new Position(new Column(0), new Row(0)),
              new Position(new Column(0), new Row(i)),
              null));
    }
    assertTrue(moves.contains(aiPlayer.getLastMove()));
  }

  @Test
  void makeMoveWithBishop() {
    board.setPiece(new Position(new Column(0), new Row(0)), new Bishop(Color.WHITE));
    aiPlayer.startup(board);
    aiPlayer.createMove();
    ArrayList<Move> moves = new ArrayList<>();
    for (int i = 1; i < 8; i++) {
      moves.add(
          new Move(
              new Position(new Column(0), new Row(0)),
              new Position(new Column(i), new Row(i)),
              null));
    }
    assertTrue(moves.contains(aiPlayer.getLastMove()));
  }

  @Test
  void makeMoveWithQueen() {
    board.setPiece(new Position(new Column(0), new Row(0)), new Queen(Color.WHITE));
    aiPlayer.startup(board);
    aiPlayer.createMove();
    ArrayList<Move> moves = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      moves.add(
          new Move(
              new Position(new Column(0), new Row(0)),
              new Position(new Column(i), new Row(0)),
              null));
      moves.add(
          new Move(
              new Position(new Column(0), new Row(0)),
              new Position(new Column(0), new Row(i)),
              null));
      moves.add(
          new Move(
              new Position(new Column(0), new Row(0)),
              new Position(new Column(i), new Row(i)),
              null));
    }
    assertTrue(moves.contains(aiPlayer.getLastMove()));
  }

  @Test
  void makeMoveWithKing() {
    board.setPiece(new Position(new Column(0), new Row(0)), new King(Color.WHITE));
    aiPlayer.startup(board);
    aiPlayer.createMove();
    ArrayList<Move> moves = new ArrayList<>();
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(0), new Row(1)),
            null));
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(1), new Row(0)),
            null));
    moves.add(
        new Move(
            new Position(new Column(0), new Row(0)),
            new Position(new Column(1), new Row(1)),
            null));
    assertTrue(moves.contains(aiPlayer.getLastMove()));
  }

  @Test
  void emptyBoardTest() {
    assertThrows(GameException.class, () -> aiPlayer.createMove());
  }

  @Test
  void answerDrawTest() {
    assertFalse(aiPlayer.answerDraw());
  }

  @Test
  void ifGameOverTest() {
    aiPlayer.gameOver(GameState.WHITE_WIN);
    assertThrows(GameException.class, () -> aiPlayer.createMove());
  }
}
