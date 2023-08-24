package io.deeplay.grandmastery.ui;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.BoardRender;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.helps.ConsoleHelp;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    this.printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
  }

  /**
   * Выводит на консоль текст с инструкцией для выбора типа шахмат. Ожидает ввода от пользователя и
   * возвращает выбранный тип шахмат.
   *
   * @return выбранный тип шахмат.
   * @throws IOException если возникла ошибка при чтении ввода.
   */
  @Override
  public ChessType selectChessType() throws IOException {
    printStream.println("Выберите начальную расстановку шахмат");
    printStream.println("1. Классические");
    printStream.println("2. Фишера");

    String type = expectInput(List.of("1", "2"));

    if ("1".equals(type)) {
      return ChessType.CLASSIC;
    } else {
      return ChessType.FISHERS;
    }
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
   * @param move сделанный ход
   * @param color цвет
   */
  @Override
  public void showMove(Move move, Color color) {
    printStream.println("/―――――――――――――――――――――――――――――――――――――――――――――――――――\\");
    printStream.println(
        " Ход "
            + ((color == Color.WHITE) ? "белых: " : "черных: ")
            + LongAlgebraicNotation.moveToString(move));
    printStream.println("\\―――――――――――――――――――――――――――――――――――――――――――――――――――/");
  }

  /**
   * Метод для отображения результата игры на консоли.
   *
   * @param gameState Состояние игры.
   */
  @Override
  public void showResultGame(GameState gameState) {
    if (gameState == GameState.WHITE_WIN) {
      printStream.println("Белые выиграли");
    } else if (gameState == GameState.BLACK_WIN) {
      printStream.println("Чёрные выиграли");
    } else {
      printStream.println("Ничья");
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
   * Метод для подтверждения сдачи игрока.
   *
   * @return {@code true} если да, {@code false} если нет.
   * @throws IOException если произошла ошибка при считывании ввода из консоли.
   */
  @Override
  public boolean confirmSur() throws IOException {
    printStream.print("Вы уверены что хотите сдаться: [Y/n]");
    String answer = expectInput(List.of("Y", "N", "y", "n"));
    printStream.flush();
    return "Y".equals(answer) || "y".equals(answer);
  }

  /**
   * Метод для принятия/отказа ничьи игроком.
   *
   * @return {@code true} если принял, {@code false} если отказался.
   * @throws IOException если произошла ошибка при считывании ввода из консоли.
   */
  @Override
  public boolean answerDraw() throws IOException {
    printStream.print("Вам предлагают ничью: [Y/n]");
    String answer = expectInput(List.of("Y", "N", "y", "n"));
    printStream.flush();
    return "Y".equals(answer) || "y".equals(answer);
  }

  /**
   * Метод для отображения доски на консоли.
   *
   * @param board доска, которую необходимо отобразить.
   */
  @Override
  public void showBoard(Board board, Color color) {
    BoardRender.showBoard(printStream, board, color);
    printStream.println();
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
    printStream.println("Некорректный или невозможный ход! Пожалуйста, попробуйте снова.");
    printStream.println("Пример корректного хода: e2e4.");
  }
}
