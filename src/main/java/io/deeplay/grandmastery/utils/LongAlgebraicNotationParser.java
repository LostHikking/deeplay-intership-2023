package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для парсинга ходов в формате длиннай алгебраической нотации.
 *
 * <p>Строка хода всегда состоит из 4 или из 5-ти символов.
 *
 * <p>e2e4 - обычный ход.
 *
 * <p>e1g1 - рокировка.
 *
 * <p>e7e8q - превращение пешки (единственный случай 5-ти символьной записи хода).
 *
 * <p>Ходы разделяются запятой.
 */
public class LongAlgebraicNotationParser {
  /**
   * Метод проверяет валидна ли последовательность ходов для данной доски.
   *
   * @param moves Последовательность ходов
   * @param board Доска
   * @return Валидна ли доска
   */
  public static boolean validMoves(List<Move> moves, Board board) {
    var copyBoard = new HashBoard();
    BoardUtils.copyBoard(board).accept(copyBoard);

    Color color = null;

    for (Move move : moves) {
      var piece = copyBoard.getPiece(move.from());
      if (piece == null) {
        return false;
      }

      if (color == null) {
        color = piece.getColor();
      } else if (color == piece.getColor()) {
        return false;
      } else {
        color = piece.getColor();
      }

      if (move.promotionPiece() != null) {
        if (piece.canRevive(copyBoard, move)) {
          piece.revive(copyBoard, move);
        } else {
          return false;
        }
      } else if (piece.canMove(copyBoard, move)) {
        piece.move(copyBoard, move);
      } else {
        return false;
      }
    }

    return true;
  }

  /**
   * * Метод возвращает список ходов на основе входной строки.
   *
   * @param moves Строка с ходами в длинной алгебраической записи, разделённая запятыми
   * @return Список ходов
   */
  public static List<Move> getMovesFromString(String moves) {
    return Arrays.stream(moves.split(","))
        .map(LongAlgebraicNotationParser::getMoveFromString)
        .collect(Collectors.toList());
  }

  /**
   * Метод возвращает ход на основе входной строки.
   *
   * @param stringMove Строка содержащая ход в длинной алгебраической записи.
   * @return Возвращает ход, спаршенный из строки
   */
  public static Move getMoveFromString(String stringMove) {
    if (stringMove.length() == 4) {
      return getSimpleMoveFromString(stringMove);
    } else if (stringMove.length() == 5) {
      return getPromotionMoveFromString(stringMove);
    } else {
      throw GameErrorCode.INCORRECT_MOVE_FORMAT.asException();
    }
  }

  /**
   * Метод возвращает ход на основе входной строки.
   *
   * @param simpleMoveString Строка содержащая простой ход см. описание класса
   * @return Возвращает ход, спаршенный из строки
   */
  private static Move getSimpleMoveFromString(String simpleMoveString) {
    var fromPosition = Position.getPositionFromString(simpleMoveString.substring(0, 2));
    var toPosition = Position.getPositionFromString(simpleMoveString.substring(2));

    return new Move(fromPosition, toPosition, null);
  }

  /**
   * Метод возвращает ход на основе входной строки.
   *
   * @param promotionMoveString Строка содержащая ход с превращением пешки см. описание класса
   * @return Возвращает ход, спаршенный из строки
   */
  private static Move getPromotionMoveFromString(String promotionMoveString) {
    var fromPosition = Position.getPositionFromString(promotionMoveString.substring(0, 2));
    var toPosition = Position.getPositionFromString(promotionMoveString.substring(2, 4));

    var figureSymbol = promotionMoveString.charAt(4);
    var piece =
        Arrays.stream(FigureType.values())
            .filter(figureType -> figureType.getSymbol() == figureSymbol)
            .findAny()
            .orElseThrow(GameErrorCode.INCORRECT_FIGURE_CHARACTER::asException);

    return new Move(fromPosition, toPosition, piece);
  }
}
