package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Figures;
import java.util.List;

/** Класс для вычисления бонусов и штрафов в оценке доски. */
class Bonuses {

  /**
   * Вычисляет штраф в миттельшпиле (середине игры) за невыведенные фигуры.
   *
   * @param board Доска.
   * @param gameHistory История игры.
   * @param color Цвет игрока.
   * @return Штраф.
   */
  protected static double middlegame(Board board, GameHistory gameHistory, Color color) {
    double result = 0.0;
    if (gameHistory.getMoves().size() > 13) {
      List<Position> friendlies = board.getAllPiecePositionByColor(color);

      for (Position friendly : friendlies) {
        Piece piece = board.getPiece(friendly);
        if ((piece.getFigureType() == FigureType.KNIGHT
            || piece.getFigureType() == FigureType.ROOK
            || piece.getFigureType() == FigureType.BISHOP)) {
          int beginRow = color == Color.WHITE ? 0 : 7;
          result -= friendly.row().value() == beginRow ? 1.0 : 0.0;
        }
      }
    }
    return result;
  }

  /**
   * Вычисляет бонусы за открытые вертикальные и горизонтальные линии.
   *
   * @param board Доска.
   * @param color Цвет игрока.
   * @param countMove Количество сделанных ходов.
   * @return Бонусы за открытые вертикальные и горизонтальные линии.
   */
  protected static double openLines(Board board, Color color, int countMove) {
    double result = 0.0;
    if (countMove < 10) {
      return result;
    }

    List<Position> friendlies = board.getAllPiecePositionByColor(color);
    for (Position friendly : friendlies) {
      Piece piece = board.getPiece(friendly);
      if (piece.getFigureType() == FigureType.ROOK) {
        result += openVerticalAndHorizontalLines(board, color, friendly);
      } else if (piece.getFigureType() == FigureType.QUEEN) {
        result += openVerticalAndHorizontalLines(board, color, friendly);
      }
    }
    return result;
  }

  /**
   * Вычисляет бонусы за открытые вертикальные и горизонтальные линии для конкретной фигуры.
   *
   * @param board Доска.
   * @param color Цвет игрока.
   * @param pos Позиция фигуры на доске.
   * @return Бонусы за открытые вертикальные и горизонтальные линии для фигуры.
   */
  private static double openVerticalAndHorizontalLines(Board board, Color color, Position pos) {
    int row = pos.row().value();
    int col = pos.col().value();
    boolean openHorizontal = Figures.hasNotFigureBetweenCols(board, row, -1, 8, List.of(pos));
    boolean openVertical = Figures.hasNotFigureBetweenRows(board, col, -1, 8, List.of(pos));
    double result = 0.0;

    if (openVertical) {
      result += col == 3 || col == 4 ? 0.5 : 0.25;
    }

    if (openHorizontal) {
      int endRow = color == Color.WHITE ? 7 : 0;
      result += row == endRow || row == Math.abs(endRow - 1) ? 0.5 : 0.25;
    }

    return result;
  }
}
