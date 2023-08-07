package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.FigureType;

/** Класс FigureUtils предоставляет утилиты для работы с фигурами, позициями и ходами. */
public class Figures {

  /**
   * Метод валидирует позицию.
   *
   * @param position Позиция
   * @return Валидна ли позиция
   */
  public static boolean isValidPosition(Position position) {
    var rowValue = position.row().value();
    var colValue = position.col().value();

    return rowValue >= 0 && rowValue <= 7 && colValue >= 0 && colValue <= 7;
  }

  /**
   * Метод выполняет базовую валидацию хода.
   *
   * @param move Ход.
   * @return Валиден ли ход
   */
  public static boolean basicValidMove(Move move, Board board, boolean withKingCheck) {
    var figureFrom = board.getPiece(move.from());
    var figureTo = board.getPiece(move.to());

    if (withKingCheck && figureTo != null && figureTo.getFigureType() == FigureType.KING) {
      return false;
    }

    return isValidPosition(move.from())
        && isValidPosition(move.to())
        && figureFrom != null
        && (figureTo == null || (figureTo.getColor() != figureFrom.getColor()));
  }

  /**
   * Метод создаёт ход из существующей позиции и смещений относительно её.
   *
   * @param position Позиция
   * @param deltaCol Смещение относительно col
   * @param deltaRow Смещение относительно row
   * @return Ход
   */
  public static Move getMoveByPositionAndDeltas(Position position, int deltaCol, int deltaRow) {
    return new Move(
        position,
        new Position(
            new Column(position.col().value() + deltaCol),
            new Row(position.row().value() + deltaRow)),
        null);
  }

  /**
   * Метод проверяет есть ли какая либо фигура на заданной вертикали между двумя позициями.
   *
   * @param board Доска
   * @param colNumber Позиция колонки по которой мы бежим
   * @param startRow Начальная позиция по row
   * @param endRow Конечная позиция по row
   * @return Есть ли фигура
   */
  public static boolean hasFigureOnVerticalBetweenRowPosition(
      Board board, int colNumber, int startRow, int endRow) {
    for (var pos = Math.min(startRow, endRow) + 1; pos < Math.max(startRow, endRow); pos++) {
      if (board.getPiece(colNumber, pos) != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Метод проверяет есть ли какая либо фигура на заданной горизонталями между двумя позициями.
   *
   * @param board Доска
   * @param rowNumber Позиция строки по которой мы бежим
   * @param startCol Начальная позиция по col
   * @param endCol Конечная позиция по col
   * @return Есть ли фигура
   */
  public static boolean hasFigureOnHorizontalBetweenColPosition(
      Board board, int rowNumber, int startCol, int endCol) {
    for (var pos = Math.min(startCol, endCol) + 1; pos < Math.max(startCol, endCol); pos++) {
      if (board.getPiece(pos, rowNumber) != null) {
        return true;
      }
    }

    return false;
  }

  /**
   * Метод проверяет есть ли какая либо фигура на заданной диагонали между двумя позициями.
   *
   * @param board Доска
   * @param startRow Начальная позиция по row
   * @param endRow Начальная позиция по row
   * @param startCol Начальная позиция по col
   * @param endCol Конечная позиция по col
   * @return Есть ли фигура
   */
  public static boolean hasFigureOnDiagonalBetweenPositions(
      Board board, int startRow, int endRow, int startCol, int endCol) {
    int dx;
    int x = startCol;
    int dy;
    int y = startRow;
    if (startCol < endCol) {
      dx = 1;
    } else {
      dx = -1;
    }
    if (startRow < endRow) {
      dy = 1;
    } else {
      dy = -1;
    }
    while (x != endCol - dx) {
      x += dx;
      y += dy;
      if (board.getPiece(x, y) != null) {
        return true;
      }
    }
    return false;
  }
}
