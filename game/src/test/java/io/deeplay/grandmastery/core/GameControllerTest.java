package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.utils.Boards;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameControllerTest {
  private GameController gameController;
  private Board board;

  /** Метод инициализирует gameController. */
  @BeforeEach
  public void init() {
    board = new HashBoard();

    var humanPlayer = new AiPlayer(board, Color.WHITE);
    var aiPlayer = new AiPlayer(board, Color.BLACK);

    gameController = new GameController(humanPlayer, aiPlayer, ChessType.CLASSIC);
  }

  @Test
  public void beginPlayBotVsWhiteHumanTest() throws IOException {
    var humanPlayer = new HumanPlayer("TestPlayer", board, Color.WHITE, new EmptyUi());
    var aiPlayer = new AiPlayer("Bot", board, Color.BLACK, new EmptyUi());

    var newGameController = new GameController(humanPlayer, aiPlayer, ChessType.CLASSIC);
    newGameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, newGameController.getGameStatus()),
        () -> assertEquals(expectBoard, newGameController.getBoard()),
        () -> assertEquals("TestPlayer", newGameController.getWhite().getName()),
        () -> assertEquals("Bot", newGameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, newGameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, newGameController.getBlack().getColor()));
  }

  @Test
  public void beginPlayBotVsBlackHumanTest() throws IOException {
    var humanPlayer = new HumanPlayer("TestPlayer", board, Color.BLACK, new EmptyUi());
    var aiPlayer = new AiPlayer("Bot", board, Color.WHITE, new EmptyUi());

    var newGameController = new GameController(humanPlayer, aiPlayer, ChessType.CLASSIC);
    newGameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, newGameController.getGameStatus()),
        () -> assertEquals(expectBoard, newGameController.getBoard()),
        () -> assertEquals("TestPlayer", newGameController.getBlack().getName()),
        () -> assertEquals("Bot", newGameController.getWhite().getName()),
        () -> assertEquals(Color.WHITE, newGameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, newGameController.getBlack().getColor()));
  }

  @Test
  public void beginPlayBotVsBotTest() throws IOException {
    gameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertEquals(expectBoard, gameController.getBoard()),
        () -> assertEquals("Bot", gameController.getWhite().getName()),
        () -> assertEquals("Bot", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void beginPlayHumanVsHumanTest() throws IOException {
    var humanPlayer = new HumanPlayer("WhitePlayer", board, Color.WHITE, new EmptyUi());
    var humanPlayer2 = new HumanPlayer("BlackPlayer", board, Color.BLACK, new EmptyUi());

    var newGameController = new GameController(humanPlayer, humanPlayer2, ChessType.CLASSIC);
    newGameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, newGameController.getGameStatus()),
        () -> assertEquals(expectBoard, newGameController.getBoard()),
        () -> assertEquals("WhitePlayer", newGameController.getWhite().getName()),
        () -> assertEquals("BlackPlayer", newGameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, newGameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, newGameController.getBlack().getColor()));
  }
  //
  //  @Test
  //  public void makeMoveTest() throws IOException {
  //    gameController.beginPlay();
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () ->
  //
  // assertNotNull(gameController.getBoard().getPiece(Position.getPositionFromString("e4"))),
  //        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
  //        () -> assertNull(gameController.getMove()),
  //        () -> assertFalse(gameController.isGameOver()));
  //  }
  //
  //  @Test
  //  public void makeImpossibleMoveTest() throws IOException {
  //    gameController.beginPlay();
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () ->
  // assertNull(gameController.getBoard().getPiece(Position.getPositionFromString("e4"))),
  //        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
  //        () -> assertNull(gameController.getMove()));
  //  }
  //
  //  @Test
  //  public void makeMoveAfterInvalidInputTest() throws IOException {
  //    gameController.beginPlay();
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () ->
  //
  // assertNotNull(gameController.getBoard().getPiece(Position.getPositionFromString("e4"))),
  //        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
  //        () -> assertNull(gameController.getMove()));
  //  }
  //
  //  @Test
  //  public void rollbackMoveTest() throws IOException {
  //    gameController.beginPlay();
  //    for (int i = 0; i < 3; i++) {
  //      gameController.nextMove();
  //    }
  //
  //    Board expectBoard = new HashBoard();
  //    Boards.copyBoard(gameController.getBoard()).accept(expectBoard);
  //
  //    gameController.nextMove();
  //    Assertions.assertAll(
  //        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
  //        () -> assertEquals(expectBoard, gameController.getBoard()));
  //  }
  //
  //  @Test
  //  public void isGameOverWhiteWinTest() throws IOException {
  //    Board gameOverBoard = new HashBoard();
  //    gameOverBoard.setPiece(Position.getPositionFromString("h8"), new King(Color.BLACK));
  //    gameOverBoard.setPiece(Position.getPositionFromString("e1"), new King(Color.WHITE));
  //    gameOverBoard.setPiece(Position.getPositionFromString("g6"), new Queen(Color.WHITE));
  //    gameOverBoard.setPiece(Position.getPositionFromString("g1"), new Rook(Color.WHITE));
  //
  //    gameController.beginPlay();
  //    Boards.copyBoard(gameOverBoard).accept(gameController.getBoard());
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()),
  //        () -> assertTrue(gameController.isGameOver()));
  //  }
  //
  //  @Test
  //  public void isGameOverBlackWinTest() throws IOException {
  //    Board gameOverBoard = new HashBoard();
  //    gameOverBoard.setPiece(Position.getPositionFromString("h7"), new King(Color.WHITE));
  //    gameOverBoard.setPiece(Position.getPositionFromString("e1"), new King(Color.BLACK));
  //    gameOverBoard.setPiece(Position.getPositionFromString("g5"), new Queen(Color.BLACK));
  //    gameOverBoard.setPiece(Position.getPositionFromString("g1"), new Rook(Color.BLACK));
  //
  //    gameController.beginPlay();
  //    Boards.copyBoard(gameOverBoard).accept(gameController.getBoard());
  //    gameController.nextMove();
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () -> assertEquals(GameState.BLACK_WIN, gameController.getGameStatus()),
  //        () -> assertTrue(gameController.isGameOver()));
  //  }
  //
  //  @Test
  //  public void isGameOverStalemate() throws IOException {
  //    Board gameOverBoard = new HashBoard();
  //    gameOverBoard.setPiece(Position.getPositionFromString("e8"), new King(Color.BLACK));
  //    gameOverBoard.setPiece(Position.getPositionFromString("e1"), new King(Color.WHITE));
  //    gameOverBoard.setPiece(Position.getPositionFromString("b1"), new Knight(Color.WHITE));
  //    gameOverBoard.setPiece(Position.getPositionFromString("g1"), new Knight(Color.WHITE));
  //
  //    gameController.beginPlay();
  //    Boards.copyBoard(gameOverBoard).accept(gameController.getBoard());
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () -> assertEquals(GameState.STALEMATE, gameController.getGameStatus()),
  //        () -> assertTrue(gameController.isGameOver()));
  //  }
  //
  //  @Test
  //  public void makeMoveAfterGameOverTest() throws IOException {
  //    gameController.beginPlay();
  //    gameController.changeGameState(GameState.WHITE_WIN);
  //    gameController.nextMove();
  //
  //    Assertions.assertAll(
  //        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()),
  //        () -> assertTrue(gameController.isGameOver()));
  //  }
}
