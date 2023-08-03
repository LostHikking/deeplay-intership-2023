package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.FigureUtils;
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
    this.figureType = FigureType.ROOK;
    if (color == Color.WHITE) {
      this.symbol = '♖';
    } else {
      this.symbol = '♜';
    }
  }

  @Override
  public void move(Board board, Move move) {
    // TODO: Шаблонный метод
  }

  @Override
  public boolean canMove(Board board, Move move) {
    var toCol = move.to().col().value();
    var toRow = move.to().row().value();
    var fromCol = move.from().col().value();
    var fromRow = move.from().row().value();

    if (!FigureUtils.basicValidMove(move, board)) {
      return false;
    }

    if (toCol == fromCol) {
      return !FigureUtils.hasFigureOnVerticalBetweenRowPosition(board, toCol, toRow, fromRow);
    } else if (toRow == fromRow) {
      return !FigureUtils.hasFigureOnHorizontalBetweenColPosition(board, toRow, toCol, fromCol);
    }

    return false;
  }

  @Override
  public List<Move> getAllMoves(Board board, Position position) {
    var listMove = new ArrayList<Move>();

    for (int i = 0; i < 8; i++) {
      listMove.add(new Move(position, new Position(position.col(), new Row(i)), null));
      listMove.add(new Move(position, new Position(new Column(i), position.row()), null));
    }

    return listMove.stream().filter(move -> canMove(board, move)).toList();
  }

  @Override
  public void revive(Board board, Move move) {}

  @Override
  public boolean canRevive(Board board, Move move) {
    return false;
  }
}
