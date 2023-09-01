package io.deeplay.grandmastery.figures;

import static java.lang.Math.abs;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import java.util.List;

public class Queen extends Piece {
  /**
   * Конструктор для королевы.
   *
   * @param color Цвет фигуры
   */
  public Queen(Color color) {
    super(color);
    this.setFigureType(FigureType.QUEEN);
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    var toCol = move.to().col().value();
    var toRow = move.to().row().value();
    var fromRow = move.from().row().value();
    var fromCol = move.from().col().value();

    if (!Figures.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    if (toCol == fromCol) {
      return Figures.hasNotFigureBetweenRows(board, toCol, toRow, fromRow);
    } else if (toRow == fromRow) {
      return Figures.hasNotFigureBetweenCols(board, toRow, toCol, fromCol);
    } else if (abs(toCol - fromCol) == abs(toRow - fromRow)) {
      return Figures.hasNoFigureOnDiagonalBetweenPositions(board, fromRow, toRow, fromCol, toCol);
    }
    return false;
  }

  @Override
  protected List<Move> generateAllMoves(Board board, Position position) {
    List<Move> listMove = Figures.allDiagonalMoves(position);
    listMove.addAll(Figures.allVerticalAndHorizontalMoves(position));

    return listMove.stream()
        .filter(move -> canMove(board, move) && simulationMoveAndCheck(board, move))
        .toList();
  }
}
