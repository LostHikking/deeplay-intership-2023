package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.domain.Color;

/** Класс-утилита для ботов. */
public class BotUtils {
  /**
   * Функция возвращает противоположный цвет.
   *
   * @param color Цвет
   * @return Противоположный цвет
   */
  public static Color getOtherColor(Color color) {
    if (color == Color.WHITE) {
      return Color.BLACK;
    }
    return Color.WHITE;
  }
}
