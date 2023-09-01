package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Game;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Piece;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для парсинга ходов в формате длинной алгебраической нотации.
 *
 * <p>Строка хода всегда состоит из 4 или из пяти символов.
 *
 * <p>e2e4 - обычный ход.
 *
 * <p>e1g1 - рокировка.
 *
 * <p>e7e8q - превращение пешки (единственный случай пяти символьной записи хода).
 *
 * <p>Ходы разделяются запятой.
 */
public class LongAlgebraicNotation {
  /**
   * Метод проверяет валидна ли последовательность ходов для данной доски.
   *
   * @param moves Последовательность ходов
   * @param board Доска
   * @return Валидна ли доска
   */
  public static boolean validMoves(List<Move> moves, Board board) {
    var copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);
    Game game = new Game();
    game.startup(copyBoard);
    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(copyBoard);

    Piece piece = copyBoard.getPiece(moves.get(0).from());
    game.setGameState(
        piece.getColor() == Color.WHITE ? GameState.WHITE_MOVE : GameState.BLACK_MOVE);

    for (Move move : moves) {
      try {
        game.makeMove(move);
      } catch (GameException e) {
        return false;
      }
      Color enemyColor = game.getGameState() == GameState.WHITE_MOVE ? Color.BLACK : Color.WHITE;
      if (GameStateChecker.isDraw(copyBoard, gameHistory)
          || GameStateChecker.isMate(copyBoard, enemyColor)) {
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
        .map(LongAlgebraicNotation::getMoveFromString)
        .collect(Collectors.toList());
  }

  /**
   * Метод возвращает ход на основе входной строки.
   *
   * @param stringMove Строка содержащая ход в длинной алгебраической записи.
   * @return Возвращает ход, спаршенный из строки
   * @throws GameException Если строка не соответствует ожидаемому формату хода.
   */
  public static Move getMoveFromString(String stringMove) throws GameException {
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
    var fromPosition = Position.fromString(simpleMoveString.substring(0, 2));
    var toPosition = Position.fromString(simpleMoveString.substring(2));

    return new Move(fromPosition, toPosition, null);
  }

  /**
   * Метод возвращает ход на основе входной строки.
   *
   * @param promotionMoveString Строка содержащая ход с превращением пешки см. описание класса
   * @return Возвращает ход, спаршенный из строки
   */
  private static Move getPromotionMoveFromString(String promotionMoveString) {
    var fromPosition = Position.fromString(promotionMoveString.substring(0, 2));
    var toPosition = Position.fromString(promotionMoveString.substring(2, 4));

    var figureSymbol = promotionMoveString.charAt(4);
    var piece =
        Arrays.stream(FigureType.values())
            .filter(figureType -> figureType.getSymbol() == figureSymbol)
            .findAny()
            .orElseThrow(GameErrorCode.INCORRECT_FIGURE_CHARACTER::asException);

    return new Move(fromPosition, toPosition, piece);
  }

  /**
   * Преобразует объект хода в его строковое представление.
   *
   * @param move Объект хода, который требуется преобразовать в строку.
   * @return Строковое представление хода.
   */
  public static String moveToString(Move move) {
    return move.promotionPiece() != null
        ? Position.getString(move.from())
            + Position.getString(move.to())
            + move.promotionPiece().getSymbol()
        : Position.getString(move.from()) + Position.getString(move.to());
  }
}
