package io.deeplay.grandmastery.figures;

import static java.lang.Math.abs;

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

public class Queen extends Piece {
  /**
   * Конструктор для королевы.
   *
   * @param color Цвет фигуры
   */
  public Queen(Color color) {
    super(color);
    this.figureType = FigureType.QUEEN;
    if (color != Color.WHITE) {
      this.symbol = '♕';
    } else {
      this.symbol = '♛';
    }
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    var toCol = move.to().col().value();
    var toRow = move.to().row().value();
    var fromRow = move.from().row().value();
    var fromCol = move.from().col().value();

    if (!FigureUtils.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    if (toCol == fromCol) {
      return !FigureUtils.hasFigureOnVerticalBetweenRowPosition(board, toCol, toRow, fromRow);
    } else if (toRow == fromRow) {
      return !FigureUtils.hasFigureOnHorizontalBetweenColPosition(board, toRow, toCol, fromCol);
    } else if (abs(toCol - fromCol) == abs(toRow - fromRow)) {
      return !FigureUtils.hasFigureOnDiagonalBetweenPositions(
          board, fromRow, toRow, fromCol, toCol);
    }
    return false;
  }

  @Override
  public List<Move> getAllMoves(Board board, Position position) {
    var listMove = new ArrayList<Move>();
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
    for (int i = 0; i < 8; i++) {
      listMove.add(new Move(position, new Position(position.col(), new Row(i)), null));
      listMove.add(new Move(position, new Position(new Column(i), position.row()), null));
    }
    return listMove.stream().filter(move -> canMove(board, move, true)).toList();
  }

  @Override
  public void revive(Board board, Move move) {}

  @Override
  public boolean canRevive(Board board, Move move) {
    return true;
  }
}
