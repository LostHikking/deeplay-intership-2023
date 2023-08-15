package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
  /**
   * Конструктор для ладьи.
   *
   * @param color Цвет фигуры
   */
  public Rook(Color color) {
    super(color);
    this.setFigureType(FigureType.ROOK);
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    var toCol = move.to().col().value();
    var toRow = move.to().row().value();
    var fromCol = move.from().col().value();
    var fromRow = move.from().row().value();

    if (!Figures.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    if (toCol == fromCol) {
      return Figures.hasNotFigureOnVerticalBetweenRowPosition(board, toCol, toRow, fromRow);
    } else if (toRow == fromRow) {
      return !Figures.hasFigureOnHorizontalBetweenColPosition(board, toRow, toCol, fromCol);
    }

    return false;
  }

  @Override
  public List<Move> generateAllMoves(Board board, Position position) {
    var listMove = new ArrayList<Move>();

    for (int i = 0; i < 8; i++) {
      listMove.add(new Move(position, new Position(position.col(), new Row(i)), null));
      listMove.add(new Move(position, new Position(new Column(i), position.row()), null));
    }

    return listMove.stream()
        .filter(move -> canMove(board, move) && simulationMoveAndCheck(board, move))
        .toList();
  }
}
