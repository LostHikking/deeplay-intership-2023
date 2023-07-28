package io.deeplay.core;

import io.deeplay.domain.GameErrorCode;
import io.deeplay.exceptions.GameException;

import java.util.List;
import java.util.Set;

/***
 * Класс для сохранения позиции фигуры на шахматной доске
 * @since 28.07.2023
 * @author Sergei Melnikow
 */
public record Position(int col, int row) {
    private static final List<Character> validCharacters = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
    private static final Set<Character> validNumbers = Set.of('1', '2', '3', '4', '5', '6', '7', '8');

    /***
     * Метод возвращает позицию по строке
     * @param stringPos Позиция записанная в строке, например e2
     * @return Позицию на шахматной доске
     */
    public static Position getPositionFromString(String stringPos) {
        if (stringPos.length() != 2)
            throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);

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
        if (!validNumbers.contains(rowCharacter))
            throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);

        return Integer.parseInt(String.valueOf(rowCharacter));
    }

    /***
     * Метод возвращает номер столбца на шахматной доске
     * @param colCharacter Символ столбца на шахматной доске
     * @return Номер столбца на шахматной доске
     */
    private static int getColFromChar(char colCharacter) {
        if (!validCharacters.contains(colCharacter))
            throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);

        var idx = 1;

        for (Character validCharacter : validCharacters) {
            if (validCharacter == colCharacter) return idx;
            idx++;
        }

        throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);
    }
}
