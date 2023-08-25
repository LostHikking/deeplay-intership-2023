package io.deeplay.grandmastery.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.deeplay.grandmastery.domain.GameErrorCode;
import java.util.Set;

/** Класс для сохранения позиции строки фигуры на шахматной доске. */
@JsonSerialize
public record Row(int value) {
  /**
   * Конструктор, который проверяет валидность Row.
   *
   * @throws IllegalArgumentException при неверном значении, при создании Row.
   */
  public Row {
    if (value < 0 || value > 7) {
      throw new IllegalArgumentException();
    }
  }

  private static final Set<Character> VALID_NUMBERS =
      Set.of('1', '2', '3', '4', '5', '6', '7', '8');

  /**
   * Метод возвращает номер строки на шахматной доске.
   *
   * @param rowCharacter Номер строки в виде символа
   * @return Номер строки на шахматной доске
   */
  public static Row getRowFromChar(char rowCharacter) {
    if (!VALID_NUMBERS.contains(rowCharacter)) {
      throw GameErrorCode.INCORRECT_POSITION_FORMAT.asException();
    }

    return new Row(Integer.parseInt(String.valueOf(rowCharacter)) - 1);
  }

  @JsonIgnore
  public String getChar() {
    return String.valueOf(value + 1);
  }
}
