package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.BoardRender;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.PlayerInfo;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.helps.ConsoleHelp;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

/** Класс для взаимодействия с пользователем через консоль. */
public class ConsoleUi implements UI {
  /** bufferedReader для считывания ввода. */
  private final BufferedReader bufferedReader;

  /** printStream для вывода. */
  private final PrintStream printStream;

  /**
   * Конструктор класса ConsoleUi.
   *
   * @param inputStream Входной поток данных.
   * @param outputStream Выходной поток данных.
   */
  public ConsoleUi(InputStream inputStream, OutputStream outputStream) {
    this.bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
    this.printStream = new PrintStream(outputStream);
  }

  /**
   * Выводит на консоль текст с инструкцией для выбора режима игры. Ожидает ввода от пользователя и
   * возвращает выбранный режим игры.
   *
   * @return выбранный режим игры.
   * @throws IOException если возникла ошибка при чтении ввода.
   */
  @Override
  public GameMode selectMode() throws IOException {
    printStream.println("Выберите режим игры:");
    printStream.println("1. Bot vs Bot");
    printStream.println("2. Bot vs Human");
    printStream.println("3. Human vs Human");

    String mode = expectInput(List.of("1", "2", "3"));

    if ("1".equals(mode)) {
      return GameMode.BOT_VS_BOT;
    } else if ("2".equals(mode)) {
      return GameMode.HUMAN_VS_BOT;
    } else {
      return GameMode.HUMAN_VS_HUMAN;
    }
  }

  /**
   * Метод для выбора цвета игрока.
   *
   * @return выбранный цвет: Color.WHITE для белого цвета, Color.BLACK для черного цвета.
   * @throws IOException если произошла ошибка при считывании ввода из консоли.
   */
  @Override
  public Color selectColor() throws IOException {
    printStream.println("Выберете цвет: 1 - Белые, 2 - Черные");
    String input = expectInput(List.of("1", "2"));

    if ("1".equals(input)) {
      return Color.WHITE;
    } else {
      return Color.BLACK;
    }
  }

  /**
   * Метод для ввода имени игрока определенного цвета.
   *
   * @param color цвет игрока, для которого необходимо ввести имя.
   * @return введенное имя игрока.
   * @throws IOException если произошла ошибка при считывании ввода из консоли.
   */
  @Override
  public String inputPlayerName(Color color) throws IOException {
    printStream.println("Игрок " + color + " введите ваше имя: ");
    return bufferedReader.readLine();
  }

  /**
   * Метод для ожидания и считывания ввода из консоли, пока не будет введено одно из ожидаемых
   * значений.
   *
   * @param expected список ожидаемых значений.
   * @return введенное значение, которое присутствует в списке ожидаемых.
   * @throws IOException если произошла ошибка при считывании ввода из консоли.
   */
  private String expectInput(List<String> expected) throws IOException {
    while (true) {
      String input = bufferedReader.readLine();
      if (expected.contains(input)) {
        return input;
      }
      printStream.println("Пожалуйста, введите одно из возможных значений " + expected);
    }
  }

  /**
   * Метод для отображения хода игрока на консоли.
   *
   * @param board доска.
   * @param movePlayer игрок, выполняющий ход.
   */
  @Override
  public void showMove(Board board, PlayerInfo movePlayer) {
    printStream.println("/―――――――――――――――――――――――――――――――――――――――――――――――――――\\");
    printStream.println(
        " Ход игрока: "
            + movePlayer.getName()
            + "("
            + movePlayer.getColor()
            + ") "
            + LongAlgebraicNotation.moveToString(movePlayer.getMoveData()));
    printStream.println("\\―――――――――――――――――――――――――――――――――――――――――――――――――――/");

    showBoard(board);
    printStream.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    printStream.flush();
  }

  /**
   * Метод для отображения результата игры на консоли.
   *
   * @param winPlayer победитель игры, может быть null, если игра закончилась вничью.
   */
  @Override
  public void showResultGame(PlayerInfo winPlayer) {
    if (winPlayer == null) {
      printStream.println("Stalemate!");
    } else {
      printStream.println("Win " + winPlayer.getColor() + ": " + winPlayer.getName());
    }
  }

  /** Метод для вывода справки в консоль. */
  @Override
  public void printHelp() {
    printStream.println(ConsoleHelp.help);
  }

  /**
   * Метод для ввода хода игрока с указанием его имени.
   *
   * @param playerName имя игрока, который должен ввести свой ход.
   * @return строка, содержащая введенный ход.
   * @throws IOException если произошла ошибка при считывании ввода из консоли.
   */
  @Override
  public String inputMove(String playerName) throws IOException {
    printStream.print(playerName + " введите ваш ход: ");
    return bufferedReader.readLine();
  }

  /**
   * Метод для отображения доски на консоли.
   *
   * @param board доска, которую необходимо отобразить.
   */
  @Override
  public void showBoard(Board board) {
    BoardRender.showBoard(printStream, board);
  }

  @Override
  public void close() {
    try {
      bufferedReader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    printStream.close();
  }

  /** Метод для вывода сообщения о некорректном ходе на консоль. */
  @Override
  public void incorrectMove() {
    printStream.println("Некорректный ход! Пожалуйста, введите ход правильно.");
    printStream.println("Пример хода: e2e4.");
  }

  /**
   * Метод для вывода сообщения о пустой начальной позиции на консоль.
   *
   * @param move ход, который начинается с пустой клетки.
   */
  @Override
  public void emptyStartPosition(Move move) {
    printStream.println("На клетке " + Position.positionToString(move.from()) + " пусто!");
  }

  /**
   * Метод для вывода сообщения о невозможности хода на консоль.
   *
   * @param move ход, который невозможно выполнить.
   */
  @Override
  public void moveImpossible(Move move) {
    printStream.println("Ход " + LongAlgebraicNotation.moveToString(move) + " невозможен");
  }

  /**
   * Метод для вывода предупреждения о том, что король игрока находится под шахом после хода.
   *
   * @param move ход, который привел к шаху короля игрока.
   */
  @Override
  public void warningYourKingInCheck(Move move) {
    printStream.println(
        "Ваш король все еще под шахом, после хода: " + LongAlgebraicNotation.moveToString(move));
  }
}
