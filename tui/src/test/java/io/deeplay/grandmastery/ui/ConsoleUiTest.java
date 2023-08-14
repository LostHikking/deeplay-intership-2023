package io.deeplay.grandmastery.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.helps.ConsoleHelp;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

  /**
   * Параметризованный тест для метода selectChessType().
   *
   * @param testInput строка с вводом, передаваемая в тест в качестве потока данных.
   * @param type ожидаемый тип после выбора.
   * @throws IOException в случае возникновения исключения при выполнении теста.
   */
  @ParameterizedTest
  @CsvSource(value = {"1, CLASSIC", "2, FISHERS"})
  public void selectChessTypeTest(String testInput, String type) throws IOException {
    testInput += "\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    consoleUi = new ConsoleUi(inputStream, output);

    ChessType selectedColor = consoleUi.selectChessType();
    String expect = """
Выберите начальную расстановку шахмат
1. Классические
2. Фишера
        """;

    Assertions.assertAll(
        () -> assertEquals(ChessType.valueOf(type), selectedColor),
        () -> assertEquals(expect, output.toString(Charset.defaultCharset())));
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
  public void showMoveTest() {
    Board board = new HashBoard();
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Boards.defaultChess().accept(board);
    Player player = new HumanPlayer("Dima", Color.WHITE, consoleUi);

    player.setLastMove("e2e4");
    board.removePiece(Position.getPositionFromString("e2"));
    board.setPiece(Position.getPositionFromString("e4"), new Pawn(Color.WHITE));
    consoleUi.showMove(board, player);

    String expect =
        """
/―――――――――――――――――――――――――――――――――――――――――――――――――――\\
 Ход игрока: Dima(WHITE) e2e4
\\―――――――――――――――――――――――――――――――――――――――――――――――――――/
┏━━━━━━━━━━━━━━━━━━━━━━┓
┃  │♜│♞│♝│♚│♛│♝│♞│♜│ 1 ┃
┃  │♟│♟│♟│ │♟│♟│♟│♟│ 2 ┃
┃  │ │ │ │ │ │ │ │ │ 3 ┃
┃  │ │ │ │♟│ │ │ │ │ 4 ┃
┃  │ │ │ │ │ │ │ │ │ 5 ┃
┃  │ │ │ │ │ │ │ │ │ 6 ┃
┃  │♙│♙│♙│♙│♙│♙│♙│♙│ 7 ┃
┃  │♖│♘│♗│♔│♕│♗│♘│♖│ 8 ┃
┃   h g f e d c b a    ┃
┗━━━━━━━━━━━━━━━━━━━━━━┛
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """;
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void showResulWinnerGameTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.WHITE_WIN);

    assertEquals("Белые выиграли\n", output.toString(Charset.defaultCharset()));
  }

  @Test
  public void showResulStalemateTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.DRAW);
    assertEquals("Ничья\n", output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printIncorrectMoveTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.incorrectMove();
    String expect = "Некорректный ход! Пожалуйста, введите ход правильно.\nПример хода: e2e4.\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printEmptyStartPositionTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    consoleUi.emptyStartPosition(move);
    String expect = "На клетке e2 пусто!\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printMoveImpossibleTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    consoleUi.moveImpossible(move);
    String expect = "Ход e2e4 невозможен\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printWarningYourKingInCheckTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    Move move = LongAlgebraicNotation.getMoveFromString("e2e4");
    consoleUi.warningYourKingInCheck(move);
    String expect = "Ваш король все еще под шахом, после хода: e2e4\n";
    assertEquals(expect, output.toString(Charset.defaultCharset()));
  }

  @Test
  public void printHelpTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.printHelp();
    assertEquals(ConsoleHelp.help + "\n", output.toString(Charset.defaultCharset()));
  }
}
