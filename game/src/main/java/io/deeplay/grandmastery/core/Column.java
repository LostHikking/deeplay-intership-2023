package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.GameErrorCode;
import java.util.Map;
import java.util.Objects;

/** Класс для сохранения позиции столбца фигуры на шахматной доске. */
public record Column(int value) {
  private static final Map<Character, Integer> VALID_CHARACTERS =
      Map.of('a', 0, 'b', 1, 'c', 2, 'd', 3, 'e', 4, 'f', 5, 'g', 6, 'h', 7);

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

  /**
   * Возвращает символ столбца на основе значения value.
   *
   * @return Символ столбца, соответствующий значению value, или null, если значение не найдено в
   *     VALID_CHARACTERS.
   */
  public String getChar() {
    return Objects.requireNonNull(
            VALID_CHARACTERS.keySet().stream()
                .filter(key -> VALID_CHARACTERS.get(key).equals(value))
                .findFirst()
                .orElse(null))
        .toString();
  }
}
