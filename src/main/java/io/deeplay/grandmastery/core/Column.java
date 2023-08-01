package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.GameErrorCode;
import java.util.Map;

/** Класс для сохранения позиции столбца фигуры на шахматной доске. */
public record Column(int value) {
  private static final Map<Character, Integer> VALID_CHARACTERS =
      Map.of('a', 1, 'b', 2, 'c', 3, 'd', 4, 'e', 5, 'f', 6, 'g', 7, 'h', 8);

  /**
   * Метод возвращает номер столбца на шахматной доске.
   *
   * @param colCharacter Символ столбца на шахматной доске
   * @return Номер столбца на шахматной доске
   */
  public static Column getColFromChar(char colCharacter) {
    if (!VALID_CHARACTERS.containsKey(colCharacter)) {
      throw GameErrorCode.INCORRECT_POSITION_FORMAT.asException();
    }

    var key =
        VALID_CHARACTERS.keySet().stream()
            .filter(ch -> ch.equals(colCharacter))
            .findAny()
            .orElseThrow(GameErrorCode.INCORRECT_MOVE_FORMAT::asException);

    return new Column(VALID_CHARACTERS.get(key));
  }
}
