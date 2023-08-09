package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
  private Game game;

  private Board board;

  /** Белый король. */
  private static Piece whiteKing;

  /** Черный король. */
  private static Piece blackKing;

  /** Начальная позиция белого король. */
  private static Position defaultWhiteKing;

  /** Начальная позиция черного король. */
  private static Position defaultBlackKing;

  /** Инициализация королей. */
  @BeforeAll
  public static void initKings() {
    whiteKing = new King(Color.WHITE);
    blackKing = new King(Color.BLACK);

    defaultBlackKing = Position.getPositionFromString("e8");
    defaultWhiteKing = Position.getPositionFromString("e1");
  }

  /** Инициализация игры. */
  @BeforeEach
  public void init() {
    game = new Game();
    board = new HashBoard();
    GameHistory gameHistory = new GameHistory();
    game.setGameState(GameState.WHITE_MOVE);
    game.setGameHistoryListener(gameHistory);

    board.setPiece(defaultWhiteKing, whiteKing);
    board.setPiece(defaultBlackKing, blackKing);
  }

  @Test
  public void startupTest() {
    game.startup(board);

    Assertions.assertAll(
        () -> assertNotNull(board), () -> assertEquals(GameState.WHITE_MOVE, game.getGameState()));
  }

  @Test
  public void makeMoveTest() {
    Position from = Position.getPositionFromString("e2");
    Position to = Position.getPositionFromString("e4");
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(from, pawn);

    Move move = new Move(from, to, null);
    game.makeMove(move, board);

    Assertions.assertAll(
        () -> assertEquals(pawn, board.getPiece(to)),
        () -> assertNull(board.getPiece(from)),
        () -> assertEquals(GameState.BLACK_MOVE, game.getGameState()));
  }

  @Test
  public void startPositionEmptyTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e4e5");
    game.makeMove(move, board);

    assertEquals(GameState.IMPOSSIBLE_MOVE, game.getGameState());
  }

  @Test
  public void moveImpossibleTest() {
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(Position.getPositionFromString("e2"), pawn);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e5");
    game.makeMove(move, board);

    assertEquals(GameState.IMPOSSIBLE_MOVE, game.getGameState());
  }

  @Test
  public void yourKingCheckTest() {
    Piece enemyRook = new Rook(Color.BLACK);
    Position kingPosition = Position.getPositionFromString("e2");
    Position rookPosition = Position.getPositionFromString("e7");

    board.removePiece(defaultWhiteKing);
    board.setPiece(rookPosition, enemyRook);
    board.setPiece(kingPosition, whiteKing);

    Move move = LongAlgebraicNotation.getMoveFromString("e2e3");
    game.makeMove(move, board);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, game.getPrevGameState()),
        () -> assertEquals(GameState.ROLLBACK, game.getGameState()));
  }

  @Test
  public void yourKingEscapeCheckTest() {
    Piece enemyRook = new Rook(Color.BLACK);
    Position kingPosition = Position.getPositionFromString("e2");
    Position rookPosition = Position.getPositionFromString("e7");

    board.removePiece(defaultWhiteKing);
    board.setPiece(rookPosition, enemyRook);
    board.setPiece(kingPosition, whiteKing);
    Move move = LongAlgebraicNotation.getMoveFromString("e2d2");
    game.makeMove(move, board);

    Assertions.assertAll(
        () -> assertEquals(whiteKing, board.getPiece(move.to())),
        () -> assertEquals(GameState.BLACK_MOVE, game.getGameState()));
  }

  @Test
  public void enemyKingCheckTest() {
    Piece yourQueen = new Queen(Color.WHITE);
    Position kingPosition = Position.getPositionFromString("h8");
    Position queenPosition = Position.getPositionFromString("d6");

    board.removePiece(defaultBlackKing);
    board.setPiece(queenPosition, yourQueen);
    board.setPiece(kingPosition, blackKing);
    Move move = LongAlgebraicNotation.getMoveFromString("d6d8");
    game.makeMove(move, board);

    assertEquals(GameState.BLACK_MOVE, game.getGameState());
  }

  @Test
  public void isMateTest() {
    Piece whiteQueen = new Queen(Color.WHITE);
    Piece whiteRook = new Rook(Color.WHITE);
    Position kingPosition = Position.getPositionFromString("h8");
    Position queenPosition = Position.getPositionFromString("g6");
    Position rookPosition = Position.getPositionFromString("g1");

    board.removePiece(defaultBlackKing);
    board.setPiece(queenPosition, whiteQueen);
    board.setPiece(kingPosition, blackKing);
    board.setPiece(rookPosition, whiteRook);
    Move move = LongAlgebraicNotation.getMoveFromString("g6g7");
    game.makeMove(move, board);

    assertEquals(GameState.WHITE_WIN, game.getGameState());
  }

  @Test
  public void isDrawTest() {
    Piece knightOne = new Knight(Color.WHITE);
    Piece knightTwo = new Knight(Color.WHITE);

    board.setPiece(Position.getPositionFromString("b1"), knightOne);
    board.setPiece(Position.getPositionFromString("g1"), knightTwo);
    Move move = LongAlgebraicNotation.getMoveFromString("b1c3");
    game.makeMove(move, board);

    assertEquals(GameState.STALEMATE, game.getGameState());
  }
}
