package io.deeplay.service;

import io.deeplay.core.Move;
import io.deeplay.core.Position;
import io.deeplay.domain.FigureType;
import io.deeplay.domain.GameErrorCode;
import io.deeplay.exceptions.GameException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Сервисный класс для парсинга ходов в формате длиннай алгебраической нотации
 * @since 28.07.2023
 * @author Sergei Melnikow
 <p>Строка хода всегда состоит из 4 или из 5-ти символов.</p>
 <p>e2e4 - обычный ход.</p>
 <p>e1g1 - рокировка.</p>
 <p>e7e8q - превращение пешки (единственный случай 5-ти символьной записи хода).</p>
 <p>Ходы разделяются запятой.</p> */

public class LongAlgebraicNotationService {
    /***
     * Метод возвращает список ходов на основе входной строки
     @param moves Строка с ходами в длинной алгебраической записи, разделённая запятыми
     @return Список ходов
    */
    public static List<Move> getMovesFromString(String moves) {
        return Arrays.stream(moves.split(","))
                .map(LongAlgebraicNotationService::getMoveFromString)
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает ход на основе входной строки
     @param stringMove Строка содержащая ход в длинной алгебраической записи.
     @return Возвращает ход, спаршенный из строки
     * */
    public static Move getMoveFromString(String stringMove) {
        if (stringMove.length() == 4)
            return getSimpleMoveFromString(stringMove);
        else if (stringMove.length() == 5)
            return getPromotionMoveFromString(stringMove);
        else
            throw new GameException(GameErrorCode.INCORRECT_MOVE_FORMAT);
    }

    /**
     * @param simpleMoveString Строка содержащая простой ход см. описание класса
     * @return Возвращает ход, спаршенный из строки
     * */
    private static Move getSimpleMoveFromString(String simpleMoveString) {
        var fromPosition = Position.getPositionFromString(simpleMoveString.substring(0, 2));
        var toPosition = Position.getPositionFromString(simpleMoveString.substring(2));

        return new Move(fromPosition, toPosition, null);
    }

    /**
     * @param promotionMoveString Строка содержащая ход с превращением пешки см. описание класса
     * @return Возвращает ход, спаршенный из строки
     * */
    private static Move getPromotionMoveFromString(String promotionMoveString) {
        var fromPosition = Position.getPositionFromString(promotionMoveString.substring(0, 2));
        var toPosition = Position.getPositionFromString(promotionMoveString.substring(2, 4));

        var figureSymbol = promotionMoveString.charAt(4);
        var piece = Arrays.stream(FigureType.values())
                .filter(figureType -> figureType.getSymbol() == figureSymbol)
                .findAny()
                .orElseThrow(() -> new GameException(GameErrorCode.INCORRECT_FIGURE_CHARACTER));

        return new Move(fromPosition, toPosition, piece);
    }
}
