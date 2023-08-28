package io.deeplay.grandmastery.domain;

public enum Color {
  WHITE,
  BLACK;

  /**
   * Функция возвращает противоположный цвет.
   *
   * @return Противоположный цвет
   */
  public Color getOpposite() {
    if (this == Color.WHITE) {
      return Color.BLACK;
    }
    return Color.WHITE;
  }
}
