package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.Boards;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameControllerTest {
  private GameController gameController;

  private final UI testUi = mock(UI.class);

  /** Создает gameController по умолчанию с двумя игроками людьми. */
  @BeforeEach
  public void init() {
    Player white = new HumanPlayer("White", Color.WHITE, testUi, false);
    Player black = new HumanPlayer("Black", Color.BLACK, testUi, false);
    gameController = new GameController(white, black);
  }

  @Test
  public void beginPlayClassicTest() {
    gameController.beginPlay(ChessType.CLASSIC);

    Board expectBoard = new HashBoard();
    Boards.defaultChess().accept(expectBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertTrue(Boards.isEqualsBoards(expectBoard, gameController.getBoard())),
        () -> assertEquals("White", gameController.getWhite().getName()),
        () -> assertEquals("Black", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void beginPlayFishersTest() {
    gameController.beginPlay(ChessType.FISHERS);

    Board defaultBoard = new HashBoard();
    Boards.defaultChess().accept(defaultBoard);

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertNotEquals(defaultBoard, gameController.getBoard()),
        () -> assertEquals("White", gameController.getWhite().getName()),
        () -> assertEquals("Black", gameController.getBlack().getName()),
        () -> assertEquals(Color.WHITE, gameController.getWhite().getColor()),
        () -> assertEquals(Color.BLACK, gameController.getBlack().getColor()));
  }

  @Test
  public void makeMoveTest() throws IOException {
    when(testUi.inputMove(anyString())).thenReturn("e2e4");

    gameController.beginPlay(ChessType.CLASSIC);
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertNotNull(gameController.getBoard().getPiece(Position.fromString("e4"))),
        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
        () -> assertNull(gameController.getMove()),
        () -> assertFalse(gameController.isGameOver()));
  }

  @Test
  public void makeImpossibleMoveTest() throws IOException {
    when(testUi.inputMove(anyString())).thenReturn("e3e4");

    gameController.beginPlay(ChessType.CLASSIC);

    Assertions.assertAll(
        () -> assertThrows(GameException.class, gameController::nextMove),
        () -> assertNull(gameController.getBoard().getPiece(Position.fromString("e4"))),
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()),
        () -> assertNull(gameController.getMove()));
  }

  @Test
  public void kingCheckAfterMoveTest() throws IOException {
    when(testUi.inputMove(anyString())).thenReturn("e2e4", "e7e5", "d1h5", "f7f6");

    gameController.beginPlay(ChessType.CLASSIC);
    for (int i = 0; i < 3; i++) {
      gameController.nextMove();
    }

    Board expectBoard = gameController.getBoard();
    Assertions.assertAll(
        () -> assertThrows(GameException.class, gameController::nextMove),
        () -> assertEquals(GameState.BLACK_MOVE, gameController.getGameStatus()),
        () -> assertTrue(Boards.isEqualsBoards(expectBoard, gameController.getBoard())));
  }

  @Test
  public void isGameOverWhiteWinTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("e2e4", "f1c4", "d1h5", "h5f7");
    when(testUi.inputMove("Black")).thenReturn("e7e5", "a7a6", "a6a5");
    gameController.beginPlay(ChessType.CLASSIC);
    for (int i = 0; i < 7; i++) {
      gameController.nextMove();
    }

    Assertions.assertAll(
        () -> assertTrue(gameController.isGameOver()),
        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()));
  }

  @Test
  public void isGameOverBlackWinTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("e2e4", "h2h4", "a2a4", "a4a5");
    when(testUi.inputMove("Black")).thenReturn("e7e5", "f8c5", "d8h4", "h4f2");
    gameController.beginPlay(ChessType.CLASSIC);
    for (int i = 0; i < 8; i++) {
      gameController.nextMove();
    }

    Assertions.assertAll(
        () -> assertTrue(gameController.isGameOver()),
        () -> assertEquals(GameState.BLACK_WIN, gameController.getGameStatus()));
  }

  @Test
  public void makeMoveAfterGameOverTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("e2e4", "f1c4", "d1h5", "h5f7");
    when(testUi.inputMove("Black")).thenReturn("e7e5", "a7a6", "a6a5");
    gameController.beginPlay(ChessType.CLASSIC);
    for (int i = 0; i < 7; i++) {
      gameController.nextMove();
    }

    Assertions.assertAll(
        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()),
        () -> assertTrue(gameController.isGameOver()));
  }

  @Test
  public void isGameOverStalemateTest() throws IOException {
    when(testUi.inputMove("White"))
        .thenReturn("e2e3", "d1h5", "h5a5", "a5c7", "h2h4", "c7d7", "d7b7", "b7b8", "b8c8", "c8e6");
    when(testUi.inputMove("Black"))
        .thenReturn("a7a5", "a8a6", "h7h5", "a6h6", "f7f6", "e8f7", "d8d3", "d3h7", "f7g6");
    gameController.beginPlay(ChessType.CLASSIC);
    for (int i = 0; i < 19; i++) {
      gameController.nextMove();
    }

    Assertions.assertAll(
        () -> assertTrue(gameController.isGameOver()),
        () -> assertEquals(GameState.DRAW, gameController.getGameStatus()));
  }

  @Test
  public void whiteSurrenderTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("sur");
    when(testUi.confirmSur()).thenReturn(true);
    gameController.beginPlay(ChessType.CLASSIC);
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertTrue(gameController.isGameOver()),
        () -> assertEquals(GameState.BLACK_WIN, gameController.getGameStatus()));
  }

  @Test
  public void blackSurrenderTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("e2e4");
    when(testUi.inputMove("Black")).thenReturn("sur");
    when(testUi.confirmSur()).thenReturn(true);

    gameController.beginPlay(ChessType.CLASSIC);
    gameController.nextMove();
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertTrue(gameController.isGameOver()),
        () -> assertEquals(GameState.WHITE_WIN, gameController.getGameStatus()));
  }

  @Test
  public void whiteOfferDrawAndBlackAcceptTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("draw");
    when(testUi.answerDraw()).thenReturn(true);
    gameController.beginPlay(ChessType.CLASSIC);
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertTrue(gameController.isGameOver()),
        () -> assertEquals(GameState.DRAW, gameController.getGameStatus()));
  }

  @Test
  public void whiteOfferDrawAndBlackRefusedTest() throws IOException {
    when(testUi.inputMove("White")).thenReturn("draw");
    when(testUi.answerDraw()).thenReturn(false);
    gameController.beginPlay(ChessType.CLASSIC);
    gameController.nextMove();

    Assertions.assertAll(
        () -> assertFalse(gameController.isGameOver()),
        () -> assertEquals(GameState.WHITE_MOVE, gameController.getGameStatus()));
  }
}
