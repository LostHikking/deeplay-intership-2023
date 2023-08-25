package io.deeplay.grandmastery.core;

import com.sun.tools.javac.Main;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Интерфейс UI предоставляет методы для взаимодействия с пользовательским интерфейсом. */
public interface UI {
  /**
   * Выбор режима игры.
   *
   * @return Выбранный режим игры.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  GameMode selectMode() throws IOException;

  /**
   * Выбор типа шахмат.
   *
   * @return Выбранный тип шахмат.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  ChessType selectChessType() throws IOException;

  /**
   * Выбор цвета игрока.
   *
   * @return Выбранный цвет игрока.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  Color selectColor() throws IOException;

  /**
   * Ввод имени игрока.
   *
   * @param color Цвет игрока.
   * @return Введенное имя игрока.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  String inputPlayerName(Color color) throws IOException;

  /**
   * Отображение хода игры.
   *
   * @param move Ход игры.
   * @param color Цвет игрока.
   */
  void showMove(Move move, Color color);

  /**
   * Отображение результата игры.
   *
   * @param gameState Состояние игры.
   */
  void showResultGame(GameState gameState);

  /** Отображение справки. */
  void printHelp();

  /**
   * Отображение доски игры.
   *
   * @param board Доска игры.
   * @param color Цвет игрока.
   */
  void showBoard(Board board, Color color);

  /** Отображение неверного хода. */
  void incorrectMove();

  /**
   * Ввод хода игрока.
   *
   * @param playerName Имя игрока.
   * @return Введенный ход.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  String inputMove(String playerName) throws IOException;

  /**
   * Подтверждение сдачи партии.
   *
   * @return true, если игрок подтверждает сдачу; в противном случае - false.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  boolean confirmSur() throws IOException;

  /**
   * Ответ на ничью.
   *
   * @return true, если игрок соглашается на ничью; в противном случае - false.
   * @throws IOException В случае ошибки ввода/вывода.
   */
  boolean answerDraw() throws IOException;

  /** Закрытие пользовательского интерфейса. */
  void close();

  /** Выводит сообщение пользователю. */
  void printEventMessage(String message);

  /**
   * Достает название ui из конфига в своем модуле.
   *
   * @return название ui.
   * @throws IOException в случаи ошибки с открытием/чтением конфига.
   */
  static String getUiFromConfig() throws IOException {
    try (InputStream config =
        Main.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties properties = new Properties();
      properties.load(config);

      return properties.getProperty("ui");
    }
  }
}
