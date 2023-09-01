package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HumanPlayerTest {
  private final UI ui = mock(UI.class);
  private Player testPlayer;

  public void init() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui, false);
  }

  @Test
  public void createMoveValidInputTest() throws GameException, IOException {
    init();
    String testInput = "e2e4";
    Move expectedMove = LongAlgebraicNotation.getMoveFromString("e2e4");

    when(ui.inputMove(anyString())).thenReturn(testInput);
    testPlayer.createMove();
    assertEquals(expectedMove, testPlayer.getLastMove());
  }

  @Test
  public void invalidInputWithoutUiTest() throws IOException {
    init();
    String testInput = "e2k4\n";

    when(ui.inputMove(anyString())).thenReturn(testInput);
    Assertions.assertAll(
        () -> assertThrows(GameException.class, testPlayer::createMove),
        () -> verify(ui, times(0)).incorrectMove());
  }

  @Test
  public void invalidInputWithUiTestTest() throws IOException {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    String testInput = "e2k4\n";

    when(ui.inputMove(anyString())).thenReturn(testInput);
    Assertions.assertAll(
        () -> assertThrows(GameException.class, testPlayer::createMove),
        () -> verify(ui, times(1)).incorrectMove());
  }

  @Test
  public void makeMoveWithoutUiTest() {
    init();
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    Move move = new Move(Position.fromString("e2"), Position.fromString("e4"), null);

    testPlayer.startup(board);
    testPlayer.setLastMove(move);
    testPlayer.makeMove(move);

    Assertions.assertAll(
        () -> assertEquals(move, testPlayer.getLastMove()),
        () -> verify(ui, times(0)).showMove(any(), any()),
        () -> verify(ui, times(0)).showBoard(any(), any()),
        () -> verify(ui, times(0)).incorrectMove());
  }

  @Test
  public void makeOursMoveWithUiTest() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    Move move = new Move(Position.fromString("e2"), Position.fromString("e4"), null);

    testPlayer.startup(board);
    testPlayer.setLastMove(move);
    testPlayer.makeMove(move);

    Assertions.assertAll(
        () -> assertEquals(move, testPlayer.getLastMove()),
        () -> verify(ui, times(1)).showMove(any(), any()),
        () -> verify(ui, times(2)).showBoard(any(), any()),
        () -> verify(ui, times(0)).incorrectMove());
  }

  @Test
  public void makeNotOursMoveWithUiTest() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    Move move = new Move(Position.fromString("e2"), Position.fromString("e4"), null);

    testPlayer.startup(board);
    testPlayer.makeMove(move);

    Assertions.assertAll(
        () -> verify(ui, times(1)).showMove(any(), any()),
        () -> verify(ui, times(2)).showBoard(any(), any()),
        () -> verify(ui, times(0)).incorrectMove());
  }

  @Test
  public void incorrectMoveWithoutUiTest() {
    init();
    Move move = new Move(Position.fromString("e2"), Position.fromString("e2"), null);

    Assertions.assertAll(
        () -> assertThrows(GameException.class, () -> testPlayer.makeMove(move)),
        () -> assertNull(testPlayer.getLastMove()),
        () -> verify(ui, times(0)).incorrectMove());
  }

  @Test
  public void incorrectMoveWithUiTest() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    Move move = new Move(Position.fromString("e2"), Position.fromString("e2"), null);

    Assertions.assertAll(
        () -> assertThrows(GameException.class, () -> testPlayer.makeMove(move)),
        () -> assertNull(testPlayer.getLastMove()),
        () -> verify(ui, times(1)).incorrectMove());
  }

  @Test
  public void startWithoutUiTest() {
    init();
    Board expect = new HashBoard();
    Boards.defaultChess().accept(expect);
    testPlayer.startup(expect);

    Assertions.assertAll(
        () -> assertTrue(Boards.equals(expect, testPlayer.getBoard()), "Equals boards"),
        () -> assertNull(testPlayer.getLastMove()),
        () -> verify(ui, times(0)).printHelp(),
        () -> verify(ui, times(0)).showBoard(any(), any()));
  }

  @Test
  public void startWithUiTest() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    Board expect = new HashBoard();
    Boards.defaultChess().accept(expect);
    testPlayer.startup(expect);

    Assertions.assertAll(
        () -> assertTrue(Boards.equals(expect, testPlayer.getBoard()), "Equals boards"),
        () -> assertNull(testPlayer.getLastMove()),
        () -> verify(ui, times(1)).printHelp(),
        () -> verify(ui, times(1)).showBoard(any(), any()));
  }

  @Test
  public void doubleStartWithUiTest() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    Board expect = new HashBoard();
    Boards.defaultChess().accept(expect);
    testPlayer.startup(expect);

    Assertions.assertAll(
        () -> assertTrue(Boards.equals(expect, testPlayer.getBoard()), "Equals boards"),
        () -> assertNull(testPlayer.getLastMove()),
        () -> verify(ui, times(1)).printHelp(),
        () -> verify(ui, times(1)).showBoard(any(), any()));
  }

  @Test
  public void throwExceptionTest() throws IOException {
    init();
    when(ui.inputMove(anyString())).thenThrow(IOException.class);
    assertThrows(RuntimeException.class, testPlayer::createMove);
  }

  /**
   * Тестовый случай для проверки сдачи в игре.
   *
   * @param input Входная строка.
   * @throws IOException В случае ошибки ввода-вывода.
   */
  @ParameterizedTest
  @ValueSource(strings = {"sur", "surrender"})
  public void surrenderTest(String input) throws IOException {
    init();
    when(ui.inputMove(anyString())).thenReturn(input);
    when(ui.confirmSur()).thenReturn(true);

    assertEquals(MoveType.SURRENDER, testPlayer.createMove().moveType());
  }

  @Test
  public void noSurrenderTest() throws IOException {
    init();
    when(ui.inputMove(anyString())).thenReturn("sur", "e2e4");
    when(ui.confirmSur()).thenReturn(false);

    testPlayer.createMove();
    Move expectMove = LongAlgebraicNotation.getMoveFromString("e2e4");
    assertEquals(expectMove, testPlayer.getLastMove());
  }

  @Test
  public void offerDrawTest() throws IOException {
    init();
    when(ui.inputMove(anyString())).thenReturn("draw");
    assertEquals(MoveType.DRAW_OFFER, testPlayer.createMove().moveType());
  }

  @Test
  public void acceptDrawTest() throws IOException {
    init();
    when(ui.answerDraw()).thenReturn(true);
    assertTrue(testPlayer.answerDraw());
  }

  @Test
  public void refuseDrawTest() throws IOException {
    init();
    when(ui.answerDraw()).thenReturn(false);
    assertFalse(testPlayer.answerDraw());
  }

  @Test
  public void gameOverWithoutUiTest() {
    init();
    testPlayer.gameOver(GameState.WHITE_WIN);
    verify(ui, times(0)).showResultGame(GameState.WHITE_WIN);
  }

  @Test
  public void gameOverWithUiTest() {
    testPlayer = new HumanPlayer("TestPlayer", Color.WHITE, ui);
    testPlayer.gameOver(GameState.WHITE_WIN);
    verify(ui, times(1)).showResultGame(GameState.WHITE_WIN);
  }

  @Test
  public void ifGameOverTest() {
    init();
    testPlayer.gameOver(GameState.WHITE_WIN);
    assertThrows(GameException.class, testPlayer::createMove);
  }

  @Test
  public void throwIoExceptionTest() throws IOException {
    init();
    when(ui.answerDraw()).thenThrow(IOException.class);
    assertThrows(RuntimeException.class, testPlayer::answerDraw);
  }
}
