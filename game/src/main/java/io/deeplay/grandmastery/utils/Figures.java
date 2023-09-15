package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.FigureType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Класс FigureUtils предоставляет утилиты для работы с фигурами, позициями и ходами. */
public class Figures {

  /**
   * Метод проверяет позицию.
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
  @SuppressWarnings("ReferenceEquality")
  public static boolean basicValidMove(
      Move move, Board board, boolean withKingCheck, boolean withColorCheck) {
    var figureFrom = board.getPiece(move.from());
    var figureTo = board.getPiece(move.to());

    if (figureFrom == null) {
      return false;
    }

    if (withKingCheck && figureTo != null && figureTo.getFigureType() == FigureType.KING) {
      return false;
    }

    if (withColorCheck && (figureTo != null && figureTo.getColor() == figureFrom.getColor())) {
      return false;
    }

    return isValidPosition(move.from()) && isValidPosition(move.to()) && figureFrom != figureTo;
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
    var colValue = position.col().value() + deltaCol;
    var rowValue = position.row().value() + deltaRow;

    if (notValidValueForPosition(colValue) || notValidValueForPosition(rowValue)) {
      return null;
    }

    return new Move(position, new Position(new Column(colValue), new Row(rowValue)), null);
  }

  private static boolean notValidValueForPosition(int value) {
    return value < 0 || value > 7;
  }

  /**
   * Метод проверяет есть ли какая-либо фигура на заданной вертикали между двумя позициями. Без
   * исключений
   *
   * @param board Доска
   * @param col Позиция колонки по которой мы бежим
   * @param startRow Начальная позиция по row
   * @param endRow Конечная позиция по row
   * @return Есть ли фигура
   */
  public static boolean hasNotFigureBetweenRows(Board board, int col, int startRow, int endRow) {
    return hasNotFigureBetweenRows(board, col, startRow, endRow, Collections.emptyList());
  }

  /**
   * Метод проверяет есть ли какая-либо фигура на заданной вертикали между двумя позициями.
   *
   * @param board Доска
   * @param col Позиция колонки по которой мы бежим
   * @param startRow Начальная позиция по row
   * @param endRow Конечная позиция по row
   * @param exception фигуры, которые будет игнорироваться, при проверке
   * @return Есть ли фигура
   */
  public static boolean hasNotFigureBetweenRows(
      Board board, int col, int startRow, int endRow, List<Position> exception) {
    for (var pos = Math.min(startRow, endRow) + 1; pos < Math.max(startRow, endRow); pos++) {
      Position position = new Position(new Column(col), new Row(pos));
      if (!exception.contains(position) && board.hasPiece(position)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Метод проверяет есть ли какая-либо фигура на заданной горизонталями между двумя позициями. Без
   * исключений
   *
   * @param board Доска
   * @param row Позиция строки по которой мы бежим
   * @param startCol Начальная позиция по col
   * @param endCol Конечная позиция по col
   * @return Есть ли фигура
   */
  public static boolean hasNotFigureBetweenCols(Board board, int row, int startCol, int endCol) {
    return hasNotFigureBetweenCols(board, row, startCol, endCol, Collections.emptyList());
  }

  /**
   * Метод проверяет есть ли какая-либо фигура на заданной горизонталями между двумя позициями.
   *
   * @param board Доска
   * @param row Позиция строки по которой мы бежим
   * @param startCol Начальная позиция по col
   * @param endCol Конечная позиция по col
   * @param exception фигуры, которые будет игнорироваться, при проверке
   * @return Есть ли фигура
   */
  public static boolean hasNotFigureBetweenCols(
      Board board, int row, int startCol, int endCol, List<Position> exception) {
    for (var pos = Math.min(startCol, endCol) + 1; pos < Math.max(startCol, endCol); pos++) {
      Position position = new Position(new Column(pos), new Row(row));
      if (!exception.contains(position) && board.hasPiece(position)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Метод проверяет есть ли какая-либо фигура на заданной диагонали между двумя позициями. Без
   * исключений
   *
   * @param board Доска
   * @param startRow Начальная позиция по row
   * @param endRow Начальная позиция по row
   * @param startCol Начальная позиция по col
   * @param endCol Конечная позиция по col
   * @return Есть ли фигура
   */
  public static boolean hasNoFigureOnDiagonalBetweenPositions(
      Board board, int startRow, int endRow, int startCol, int endCol) {
    return hasNoFigureOnDiagonalBetweenPositions(
        board, startRow, endRow, startCol, endCol, Collections.emptyList());
  }

  /**
   * Метод проверяет есть ли какая-либо фигура на заданной диагонали между двумя позициями.
   *
   * @param board Доска
   * @param startRow Начальная позиция по row
   * @param endRow Начальная позиция по row
   * @param startCol Начальная позиция по col
   * @param endCol Конечная позиция по col
   * @param exception фигуры, которые будет игнорироваться, при проверке
   * @return Есть ли фигура
   */
  public static boolean hasNoFigureOnDiagonalBetweenPositions(
      Board board, int startRow, int endRow, int startCol, int endCol, List<Position> exception) {
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
      Position position = new Position(new Column(x), new Row(y));
      if (!exception.contains(position) && board.hasPiece(position)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Генерирует список всех ходов на диагонали, доступных для данной позиции.
   *
   * @param position Позиция.
   * @return Список ходов.
   */
  public static List<Move> allDiagonalMoves(Position position) {
    List<Move> listMove = new ArrayList<>();
    int[] dx = {1, -1, 1, -1};
    int[] dy = {1, -1, -1, 1};

    for (int dir = 0; dir < 4; dir++) {
      int x = position.col().value() + dx[dir];
      int y = position.row().value() + dy[dir];
      while (0 <= x && x <= 7 && 0 <= y && y <= 7) {
        listMove.add(new Move(position, new Position(new Column(x), new Row(y)), null));
        x += dx[dir];
        y += dy[dir];
      }
    }

    return listMove;
  }

  /**
   * Генерирует список всех ходов на вертикали и горизонтали, доступных для данной позиции.
   *
   * @param position Позиция.
   * @return Список ходов.
   */
  public static List<Move> allVerticalAndHorizontalMoves(Position position) {
    List<Move> listMove = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      listMove.add(new Move(position, new Position(position.col(), new Row(i)), null));
      listMove.add(new Move(position, new Position(new Column(i), position.row()), null));
    }

    return listMove;
  }
}
