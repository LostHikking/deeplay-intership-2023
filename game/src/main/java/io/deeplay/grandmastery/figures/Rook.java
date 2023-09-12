package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import java.util.List;

public class Rook extends Piece {
  /**
   * Конструктор для ладьи.
   *
   * @param color Цвет фигуры
   */
  public Rook(Color color) {
    super(color);
    figureType = FigureType.ROOK;
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck, boolean withColorCheck) {
    var toCol = move.to().col().value();
    var toRow = move.to().row().value();
    var fromCol = move.from().col().value();
    var fromRow = move.from().row().value();

    if (!Figures.basicValidMove(move, board, withKingCheck, withColorCheck)) {
      return false;
    }

    if (toCol == fromCol) {
      return Figures.hasNotFigureBetweenRows(board, toCol, toRow, fromRow);
    } else if (toRow == fromRow) {
      return Figures.hasNotFigureBetweenCols(board, toRow, toCol, fromCol);
    }

    return false;
  }

  @Override
  protected List<Move> generateAllMoves(Board board, Position position) {
    return Figures.allVerticalAndHorizontalMoves(position).stream()
        .filter(move -> canMove(board, move) && simulationMoveAndCheck(board, move))
        .toList();
  }
}
