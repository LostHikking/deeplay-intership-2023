package io.deeplay.grandmastery.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.helps.ConsoleHelp;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ConsoleUiTest {
  private ByteArrayOutputStream output;

  private ConsoleUi consoleUi;

  @BeforeEach
  public void init() {
    output = new ByteArrayOutputStream();
  }

  @AfterEach
  public void close() {
    consoleUi.close();
  }

  /**
   * Параметризованный тест для метода selectMode().
   *
   * @param testInput строка с вводом, передаваемая в тест в качестве потока данных.
   * @param mode ожидаемый режим игры после выбора.
   * @throws IOException в случае возникновения исключения при выполнении теста.
   */
  @ParameterizedTest
  @CsvSource(value = {"1, BOT_VS_BOT", "2, HUMAN_VS_BOT", "3, HUMAN_VS_HUMAN"})
  public void selectModeTest(String testInput, String mode) throws IOException {
    testInput += "\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    consoleUi = new ConsoleUi(inputStream, output);
    GameMode selectedMode = consoleUi.selectMode();
    String expect =
        """
    Выберите режим игры:
    1. Bot vs Bot
    2. Bot vs Human
    3. Human vs Human
          """;

    Assertions.assertAll(
        () -> assertEquals(GameMode.valueOf(mode), selectedMode),
        () -> assertEquals(expect, output.toString(Charset.defaultCharset())));
  }

  /**
   * Параметризованный тест для метода selectColor().
   *
   * @param testInput строка с вводом, передаваемая в тест в качестве потока данных.
   * @param color ожидаемый цвет после выбора.
   * @throws IOException в случае возникновения исключения при выполнении теста.
   */
  @ParameterizedTest
  @CsvSource(value = {"1, WHITE", "2, BLACK"})
  public void selectColorTest(String testInput, String color) throws IOException {
    testInput += "\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    consoleUi = new ConsoleUi(inputStream, output);

    Color selectedColor = consoleUi.selectColor();

    Assertions.assertAll(
        () -> assertEquals(Color.valueOf(color), selectedColor),
        () ->
            assertEquals(
                "Выберете цвет: 1 - Белые, 2 - Черные\n",
                output.toString(Charset.defaultCharset())));
  }

  @Test
  public void incorrectSelectTest() throws IOException {
    String testInput = "3\n2\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    consoleUi = new ConsoleUi(inputStream, output);

    Color selectedColor = consoleUi.selectColor();

    Assertions.assertAll(
        () -> assertEquals(Color.BLACK, selectedColor),
        () ->
            assertTrue(
                output
                    .toString(Charset.defaultCharset())
                    .contains("Пожалуйста, введите одно из возможных значений [1, 2]")));
  }

  @Test
  public void inputPlayerNameTest() throws IOException {
    String testInput = "Dima\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    consoleUi = new ConsoleUi(inputStream, output);

    String playerName = consoleUi.inputPlayerName(Color.WHITE);

    Assertions.assertAll(
        () -> assertEquals("Dima", playerName),
        () ->
            assertEquals(
                "Игрок WHITE введите ваше имя: \n", output.toString(Charset.defaultCharset())));
  }

  @Test
  public void inputMoveTest() throws IOException {
    String testInput = "e2e4\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    consoleUi = new ConsoleUi(inputStream, output);

    String inputMove = consoleUi.inputMove("Dima");

    Assertions.assertAll(
        () -> assertEquals("e2e4", inputMove),
        () -> assertEquals("Dima введите ваш ход: ", output.toString(Charset.defaultCharset())));
  }

  @Test
  public void showMoveTest() throws IOException {
    Board board = new HashBoard();
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Boards.defaultChess().accept(board);
    Player player = new HumanPlayer("Dima", board, Color.WHITE, consoleUi);

    player.setMoveData("e2e4");
    board.removePiece(Position.getPositionFromString("e2"));
    board.setPiece(Position.getPositionFromString("e4"), new Pawn(Color.WHITE));
    consoleUi.showMove(board, player);

    String expect =
        """
/―――――――――――――――――――――――――――――――――――――――――――――――――――\\
 Ход игрока: Dima(WHITE) e2e4
\\―――――――――――――――――――――――――――――――――――――――――――――――――――/
┏━━━━━━━━━━━━━━━━━━━━━━┓
┃ 8 │♖│♘│♗│♕│♔│♗│♘│♖│  ┃
┃ 7 │♙│♙│♙│♙│♙│♙│♙│♙│  ┃
┃ 6 │ │ │ │ │ │ │ │ │  ┃
┃ 5 │ │ │ │ │ │ │ │ │  ┃
┃ 4 │ │ │ │ │♟│ │ │ │  ┃
┃ 3 │ │ │ │ │ │ │ │ │  ┃
┃ 2 │♟│♟│♟│♟│ │♟│♟│♟│  ┃
┃ 1 │♜│♞│♝│♛│♚│♝│♞│♜│  ┃
┃    a b c d e f g h   ┃
┗━━━━━━━━━━━━━━━━━━━━━━┛
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """;
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void showResulWinnerGameTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Player winner = new HumanPlayer("Dima", null, Color.WHITE, consoleUi);
    consoleUi.showResultGame(winner);

    assertEquals("Win WHITE: Dima\n", output.toString(Charset.defaultCharset()));
  }

  @Test
  public void showResulStalemateTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(null);
    assertEquals("Stalemate!\n", output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printIncorrectMoveTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.incorrectMove();
    String expect = "Некорректный ход! Пожалуйста, введите ход правильно.\nПример хода: e2e4.\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printEmptyStartPositionTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    consoleUi.emptyStartPosition(move);
    String expect = "На клетке e2 пусто!\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printMoveImpossibleTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    consoleUi.moveImpossible(move);
    String expect = "Ход e2e4 невозможен\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printWarningYourKingInCheckTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    consoleUi.warningYourKingInCheck(move);
    String expect = "Ваш король все еще под шахом, после хода: e2e4\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printHelpTest() throws IOException {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.printHelp();
    assertEquals(ConsoleHelp.help + "\n", output.toString(Charset.defaultCharset()));
  }
}
