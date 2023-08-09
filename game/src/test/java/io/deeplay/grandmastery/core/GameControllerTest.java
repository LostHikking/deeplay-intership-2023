package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import io.deeplay.grandmastery.utils.Boards;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameControllerTest {
  private GameController gameController;

  private final UI testUi = mock(UI.class);

  @BeforeEach
  public void init() {
    gameController = new GameController();
  }

  @Test
  public void beginPlayBotVsWhiteHumanTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");

    gameController.setUi(testUi);
    gameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertEquals(expectBoard, gameController.getBoard()),
        () -> assertEquals("TestPlayer", gameController.getWhite().getName()),
        () -> assertEquals("Bot", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void beginPlayBotVsBlackHumanTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.BLACK);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");

    gameController.setUi(testUi);
    gameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertEquals(expectBoard, gameController.getBoard()),
        () -> assertEquals("TestPlayer", gameController.getBlack().getName()),
        () -> assertEquals("Bot", gameController.getWhite().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void beginPlayBotVsBotTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.BOT_VS_BOT);
    gameController.setUi(testUi);
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
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_HUMAN);
    when(testUi.inputPlayerName(Color.WHITE)).thenReturn("WhitePlayer");
    when(testUi.inputPlayerName(Color.BLACK)).thenReturn("BlackPlayer");

    gameController.setUi(testUi);
    gameController.beginPlay();

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertEquals(expectBoard, gameController.getBoard()),
        () -> assertEquals("WhitePlayer", gameController.getWhite().getName()),
        () -> assertEquals("BlackPlayer", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void makeMoveTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("e2e4");

    gameController.setUi(testUi);
    gameController.beginPlay();
    gameController.nextMove();

    Assertions.assertAll(
        () ->
            assertNotNull(gameController.getBoard().getPiece(Position.getPositionFromString("e4"))),
        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
        () -> assertNull(gameController.getMove()),
        () -> assertFalse(gameController.isGameOver()));
  }

  @Test
  public void makeImpossibleMoveTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("e3e4");

    gameController.setUi(testUi);
    gameController.beginPlay();
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertNull(gameController.getBoard().getPiece(Position.getPositionFromString("e4"))),
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertNull(gameController.getMove()));
  }

  @Test
  public void makeMoveAfterInvalidInputTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("ffff", "e2e4");

    gameController.setUi(testUi);
    gameController.beginPlay();
    gameController.nextMove();

    Assertions.assertAll(
        () ->
            assertNotNull(gameController.getBoard().getPiece(Position.getPositionFromString("e4"))),
        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
        () -> assertNull(gameController.getMove()));
  }

  @Test
  public void rollbackMoveTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_HUMAN);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("e2e4", "e7e5", "d1h5", "f7f6");

    gameController.setUi(testUi);
    gameController.beginPlay();
    for (int i = 0; i < 3; i++) {
      gameController.nextMove();
    }

    Board expectBoard = new HashBoard();
    Boards.copyBoard(gameController.getBoard()).accept(expectBoard);

    gameController.nextMove();
    Assertions.assertAll(
        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
        () -> assertEquals(expectBoard, gameController.getBoard()));
  }

  @Test
  public void isGameOverWhiteWinTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("g6g7");

    Board gameOverBoard = new HashBoard();
    gameOverBoard.setPiece(Position.getPositionFromString("h8"), new King(Color.BLACK));
    gameOverBoard.setPiece(Position.getPositionFromString("e1"), new King(Color.WHITE));
    gameOverBoard.setPiece(Position.getPositionFromString("g6"), new Queen(Color.WHITE));
    gameOverBoard.setPiece(Position.getPositionFromString("g1"), new Rook(Color.WHITE));

    gameController.setUi(testUi);
    gameController.beginPlay();
    Boards.copyBoard(gameOverBoard).accept(gameController.getBoard());
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()),
        () -> assertTrue(gameController.isGameOver()));
  }

  @Test
  public void isGameOverBlackWinTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_HUMAN);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("h7h8", "g5g7");

    Board gameOverBoard = new HashBoard();
    gameOverBoard.setPiece(Position.getPositionFromString("h7"), new King(Color.WHITE));
    gameOverBoard.setPiece(Position.getPositionFromString("e1"), new King(Color.BLACK));
    gameOverBoard.setPiece(Position.getPositionFromString("g5"), new Queen(Color.BLACK));
    gameOverBoard.setPiece(Position.getPositionFromString("g1"), new Rook(Color.BLACK));

    gameController.setUi(testUi);
    gameController.beginPlay();
    Boards.copyBoard(gameOverBoard).accept(gameController.getBoard());
    gameController.nextMove();
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertEquals(GameState.BLACK_WIN, gameController.getGameStatus()),
        () -> assertTrue(gameController.isGameOver()));
  }

  @Test
  public void isGameOverStalemate() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("e1e2");

    Board gameOverBoard = new HashBoard();
    gameOverBoard.setPiece(Position.getPositionFromString("e8"), new King(Color.BLACK));
    gameOverBoard.setPiece(Position.getPositionFromString("e1"), new King(Color.WHITE));
    gameOverBoard.setPiece(Position.getPositionFromString("b1"), new Knight(Color.WHITE));
    gameOverBoard.setPiece(Position.getPositionFromString("g1"), new Knight(Color.WHITE));

    gameController.setUi(testUi);
    gameController.beginPlay();
    Boards.copyBoard(gameOverBoard).accept(gameController.getBoard());
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertEquals(GameState.STALEMATE, gameController.getGameStatus()),
        () -> assertTrue(gameController.isGameOver()));
  }

  @Test
  public void makeMoveAfterGameOverTest() throws IOException {
    when(testUi.selectMode()).thenReturn(GameMode.HUMAN_VS_BOT);
    when(testUi.selectColor()).thenReturn(Color.WHITE);
    when(testUi.inputPlayerName(any())).thenReturn("TestPlayer");
    when(testUi.inputMove(anyString())).thenReturn("g6g7");

    gameController.setUi(testUi);
    gameController.beginPlay();
    gameController.changeGameState(GameState.WHITE_WIN);
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()),
        () -> assertTrue(gameController.isGameOver()));
  }
}
