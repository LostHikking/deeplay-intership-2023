package io.deeplay.grandmastery.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
    InputStream inputStream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
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
        () ->
            assertTrue(
                output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "").contains(expect)));
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
    InputStream inputStream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    Color selectedColor = consoleUi.selectColor();

    String expect = "Выберете цвет: 1 - Белые, 2 - Черные";
    Assertions.assertAll(
        () -> assertEquals(Color.valueOf(color), selectedColor),
        () ->
            assertTrue(
                output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "").contains(expect)));
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
    InputStream inputStream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    ChessType selectedColor = consoleUi.selectChessType();
    String expect = """
Выберите начальную расстановку шахмат
1. Классические
2. Фишера
        """;

    Assertions.assertAll(
        () -> assertEquals(ChessType.valueOf(type), selectedColor),
        () ->
            assertTrue(
                output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "").contains(expect)));
  }

  @Test
  public void incorrectSelectTest() throws IOException {
    String testInput = "3\n2\n";
    InputStream inputStream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    Color selectedColor = consoleUi.selectColor();

    Assertions.assertAll(
        () -> assertEquals(Color.BLACK, selectedColor),
        () ->
            assertTrue(
                output
                    .toString(StandardCharsets.UTF_8)
                    .replaceAll("\\r", "")
                    .contains("Пожалуйста, введите одно из возможных значений [1, 2]")));
  }

  @Test
  public void inputPlayerNameTest() throws IOException {
    String testInput = "Dima\n";
    InputStream inputStream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String playerName = consoleUi.inputPlayerName(Color.WHITE);

    Assertions.assertAll(
        () -> assertEquals("Dima", playerName),
        () ->
            assertEquals(
                "Игрок WHITE введите ваше имя:", output.toString(StandardCharsets.UTF_8).trim()));
  }

  @Test
  public void inputMoveTest() throws IOException {
    String testInput = "e2e4\n";
    InputStream inputStream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String inputMove = consoleUi.inputMove("Dima");

    Assertions.assertAll(
        () -> assertEquals("e2e4", inputMove),
        () ->
            assertEquals("Dima введите ваш ход:", output.toString(StandardCharsets.UTF_8).trim()));
  }

  @Test
  public void testShowWhiteBoard() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);

    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showBoard(board, Color.WHITE);
    String expectedOutput =
        """
    8 │♖│♘│♗│♕│♔│♗│♘│♖│
    7 │♙│♙│♙│♙│♙│♙│♙│♙│
    6 │ │ │ │ │ │ │ │ │
    5 │ │ │ │ │ │ │ │ │
    4 │ │ │ │ │ │ │ │ │
    3 │ │ │ │ │ │ │ │ │
    2 │♟│♟│♟│♟│♟│♟│♟│♟│
    1 │♜│♞│♝│♛│♚│♝│♞│♜│
       a b c d e f g h""";

    expectedOutput = expectedOutput.replaceAll(" ", " ");
    expectedOutput += " ";
    assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void testShowBlackBoard() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);

    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showBoard(board, Color.BLACK);
    String expectedOutput =
        """
      │♜│♞│♝│♚│♛│♝│♞│♜│ 1
      │♟│♟│♟│♟│♟│♟│♟│♟│ 2
      │ │ │ │ │ │ │ │ │ 3
      │ │ │ │ │ │ │ │ │ 4
      │ │ │ │ │ │ │ │ │ 5
      │ │ │ │ │ │ │ │ │ 6
      │♙│♙│♙│♙│♙│♙│♙│♙│ 7
      │♖│♘│♗│♔│♕│♗│♘│♖│ 8
     \s\sh g f e d c b a""";

    expectedOutput = expectedOutput.replaceAll(" ", " ");
    expectedOutput += " ";
    assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void showWhiteMoveTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showMove(LongAlgebraicNotation.getMoveFromString("e2e4"), Color.WHITE);

    String expect =
        """
    /―――――――――――――――――――――――――――――――――――――――――――――――――――\\
     Ход белых: e2e4
    \\―――――――――――――――――――――――――――――――――――――――――――――――――――/
        """;
    assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", ""));
  }

  @Test
  public void showBlackMoveTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showMove(LongAlgebraicNotation.getMoveFromString("e7e6"), Color.BLACK);

    String expect =
        """
    /―――――――――――――――――――――――――――――――――――――――――――――――――――\\
     Ход черных: e7e6
    \\―――――――――――――――――――――――――――――――――――――――――――――――――――/
        """;
    assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", ""));
  }

  @Test
  public void showResulWhiteWinGameTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.WHITE_WIN);

    assertEquals("Белые выиграли!", output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void showResulBlackWinGameTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.BLACK_WIN);

    assertEquals("Чёрные выиграли!", output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void showResulTechDefeatWhiteGameTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.TECHNICAL_DEFEAT_WHITE);

    assertEquals(
        "Техническое поражение белых. Чёрные выиграли!",
        output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void showResulTechDefeatBlackGameTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.TECHNICAL_DEFEAT_BLACK);

    assertEquals(
        "Техническое поражение черных. Белые выиграли!",
        output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void showResulStalemateTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.showResultGame(GameState.DRAW);
    assertEquals("Ничья", output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void printEventMassage() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    String eventMassage = "Привет игрок!";
    consoleUi.printEventMessage(eventMassage);
    assertEquals(eventMassage, output.toString(StandardCharsets.UTF_8).trim());
  }

  @Test
  public void printIncorrectMoveTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.incorrectMove();

    String expect =
        """
        Некорректный или невозможный ход! Пожалуйста, попробуйте снова.
        Пример корректного хода: e2e4.
        """;
    assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", ""));
  }

  @Test
  public void printHelpTest() {
    consoleUi = new ConsoleUi(InputStream.nullInputStream(), output);
    consoleUi.printHelp();
    assertEquals(
        ConsoleHelp.help.trim(),
        output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "").trim());
  }

  /**
   * Тестовый случай для подтверждения сдачи на основе ввода пользователя.
   *
   * @param answer Ввод пользователя, ("Y" или "y").
   */
  @ParameterizedTest
  @ValueSource(strings = {"Y", "y"})
  public void confirmSurrenderTest(String answer) {
    InputStream inputStream = new ByteArrayInputStream(answer.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect = "Вы уверены что хотите сдаться: [Y/n]";
    Assertions.assertAll(
        () -> assertTrue(consoleUi.confirmSur()),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8)));
  }

  /**
   * Тестовый случай для отказа от сдачи на основе ввода пользователя.
   *
   * @param answer Ввод пользователя, ("N" или "n").
   */
  @ParameterizedTest
  @ValueSource(strings = {"N", "n"})
  public void noConfirmSurrenderTest(String answer) {
    InputStream inputStream = new ByteArrayInputStream(answer.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect = "Вы уверены что хотите сдаться: [Y/n]";
    Assertions.assertAll(
        () -> assertFalse(consoleUi.confirmSur()),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8)));
  }

  /**
   * Тестовый случай для принятия ничьей на основе ввода пользователя.
   *
   * @param answer Ввод пользователя, ("Y" или "y").
   */
  @ParameterizedTest
  @ValueSource(strings = {"Y", "y"})
  public void acceptDrawTest(String answer) {
    InputStream inputStream = new ByteArrayInputStream(answer.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect = "Вам предлагают ничью: [Y/n]";
    Assertions.assertAll(
        () -> assertTrue(consoleUi.answerDraw()),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8)));
  }

  /**
   * Тестовый случай для отказа от ничьей на основе ввода пользователя.
   *
   * @param answer Ввод пользователя, ("N" или "n").
   */
  @ParameterizedTest
  @ValueSource(strings = {"N", "n"})
  public void refuseDrawTest(String answer) {
    InputStream inputStream = new ByteArrayInputStream(answer.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect = "Вам предлагают ничью: [Y/n]";
    Assertions.assertAll(
        () -> assertFalse(consoleUi.answerDraw()),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8)));
  }

  @Test
  public void selectWhiteBotTest() {
    String input = "1\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect =
        """
    Выберите бота для белых:
    1 Randomus
    2 LjeDmitry
    3 Melniknow
        """;
    Assertions.assertAll(
        () ->
            assertEquals(
                "Randomus",
                consoleUi.selectBot(List.of("Randomus", "LjeDmitry", "Melniknow"), Color.WHITE)),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "")));
  }

  @Test
  public void selectBlackBotTest() {
    String input = "1\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect =
        """
        Выберите бота для черных:
        1 Randomus
        2 LjeDmitry
        3 Melniknow
            """;
    Assertions.assertAll(
        () ->
            assertEquals(
                "Randomus",
                consoleUi.selectBot(List.of("Randomus", "LjeDmitry", "Melniknow"), Color.BLACK)),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "")));
  }

  @Test
  public void newGameTest() {
    String input = "1\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect =
        """
            1 - начать новую игру.
            2 - закрыть приложение.
            """;
    Assertions.assertAll(
        () -> assertTrue(consoleUi.newGame()),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "")));
  }

  @Test
  public void closeGameTest() {
    String input = "2\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    consoleUi = new ConsoleUi(inputStream, output);

    String expect =
        """
                1 - начать новую игру.
                2 - закрыть приложение.
                """;
    Assertions.assertAll(
        () -> assertFalse(consoleUi.newGame()),
        () -> assertEquals(expect, output.toString(StandardCharsets.UTF_8).replaceAll("\\r", "")));
  }
}
