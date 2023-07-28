package io.deeplay.core;

import io.deeplay.domain.GameErrorCode;
import io.deeplay.exceptions.GameException;

import java.util.Set;

public record Position(int col, int row) {
    private static final Set<Character> validCharacters = Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
    private static final Set<Character> validNumbers = Set.of('1', '2', '3', '4', '5', '6', '7', '8');

    public static Position getPositionFromString(String stringPos) {
        if (stringPos.length() != 2)
            throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);

        var col = getColFromChar(stringPos.charAt(0));
        var row = getRowFromChar(stringPos.charAt(1));

        return new Position(col, row);
    }

    private static int getRowFromChar(char c) {
        if (!validNumbers.contains(c))
            throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);

        return Integer.parseInt(String.valueOf(c));
    }

    private static int getColFromChar(char c) {
        if (!validCharacters.contains(c))
            throw new GameException(GameErrorCode.INCORRECT_POSITION_FORMAT);

        return 0;
    }

    public static void main(String[] args) {
        System.out.println(getRowFromChar('1'));
    }
}
