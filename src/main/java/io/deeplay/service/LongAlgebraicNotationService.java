package io.deeplay.service;


// Строка хода всегда состоит из 4 или из 5-ти символов.
// e2e4 - обычный ход
// e1g1 - рокировка
// e7e8q - превращение пешки (единственный случай 5-ти символьной записи хода)
// Ходы разделяются запятой

import io.deeplay.core.Move;
import io.deeplay.core.Position;
import io.deeplay.domain.FigureType;
import io.deeplay.domain.GameErrorCode;
import io.deeplay.exceptions.GameException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LongAlgebraicNotationService {
    public static List<Move> getMovesFromString(String moves) {
        return Arrays.stream(moves.split(","))
                .map(LongAlgebraicNotationService::getMoveFromString)
                .collect(Collectors.toList());
    }

    public static Move getMoveFromString(String stringMove) {
        if (stringMove.length() == 4)
            return getSimpleMoveFromString(stringMove);
        else if (stringMove.length() == 5)
            return getPromotionMoveFromString(stringMove);
        else
            throw new GameException(GameErrorCode.INCORRECT_MOVE_FORMAT);
    }

    private static Move getSimpleMoveFromString(String simpleMoveString) {
        var fromPosition = Position.getPositionFromString(simpleMoveString.substring(0, 2));
        var toPosition = Position.getPositionFromString(simpleMoveString.substring(2));

        return new Move(fromPosition, toPosition, null);
    }

    private static Move getPromotionMoveFromString(String promotionMoveString) {
        var fromPosition = Position.getPositionFromString(promotionMoveString.substring(0, 2));
        var toPosition = Position.getPositionFromString(promotionMoveString.substring(2, 4));

        var figureSymbol = promotionMoveString.charAt(4);
        var piece = Arrays.stream(FigureType.values())
                .filter(figureType -> figureType.getSymbol() == figureSymbol)
                .findAny().orElseThrow(() -> new GameException(GameErrorCode.INCORRECT_FIGURE_CHARACTER));

        return new Move(fromPosition, toPosition, piece);
    }
}
