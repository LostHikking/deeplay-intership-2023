package com.grandmastery.core;

import com.grandmastery.domain.GameErrorCode;

import java.util.Map;
import java.util.Set;

/***
 * Класс для сохранения позиции фигуры на шахматной доске
 */
public record Position(int col, int row) {
    private static final Map<Character, Integer> VALID_CHARACTERS =
            Map.of('a', 1, 'b', 2, 'c', 3, 'd', 4,
                    'e', 5, 'f', 6, 'g', 7, 'h', 8);
    private static final Set<Character> VALID_NUMBERS = Set.of('1', '2', '3', '4', '5', '6', '7', '8');

    /***
     * Метод возвращает позицию по строке
     * @param stringPos Позиция записанная в строке, например e2
     * @return Позицию на шахматной доске
     */
    public static Position getPositionFromString(String stringPos) {
        if (stringPos.length() != 2)
            throw GameErrorCode.INCORRECT_POSITION_FORMAT.asException();

        var col = getColFromChar(stringPos.charAt(0));
        var row = getRowFromChar(stringPos.charAt(1));

        return new Position(col, row);
    }

    /***
     * Метод возвращает номер строки на шахматной доске
     * @param rowCharacter Номер строки в виде символа
     * @return Номер строки на шахматной доске
     */
    private static int getRowFromChar(char rowCharacter) {
        if (!VALID_NUMBERS.contains(rowCharacter))
            throw GameErrorCode.INCORRECT_POSITION_FORMAT.asException();

        return Integer.parseInt(String.valueOf(rowCharacter));
    }

    /***
     * Метод возвращает номер столбца на шахматной доске
     * @param colCharacter Символ столбца на шахматной доске
     * @return Номер столбца на шахматной доске
     */
    private static int getColFromChar(char colCharacter) {
        if (!VALID_CHARACTERS.containsKey(colCharacter))
            throw GameErrorCode.INCORRECT_POSITION_FORMAT.asException();

        var key = VALID_CHARACTERS.keySet().stream()
                .filter(ch -> ch.equals(colCharacter))
                .findAny()
                .orElseThrow(GameErrorCode.INCORRECT_MOVE_FORMAT::asException);

        return VALID_CHARACTERS.get(key);
    }
}
