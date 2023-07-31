package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.GameErrorCode;

/** Класс для сохранения позиции фигуры на шахматной доске. */
public record Position(Column col, Row row) {

  /*** Метод возвращает позицию по строке.
   *
   * @param stringPos Позиция записанная в строке, например e2
   * @return Позицию на шахматной доске
   */
  public static Position getPositionFromString(String stringPos) {
    if (stringPos.length() != 2) {
      throw GameErrorCode.INCORRECT_POSITION_FORMAT.asException();
    }

    var col = Column.getColFromChar(stringPos.charAt(0));
    var row = Row.getRowFromChar(stringPos.charAt(1));

    return new Position(col, row);
  }
}
