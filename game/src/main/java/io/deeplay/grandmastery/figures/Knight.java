package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
  /**
   * Конструктор для коня.
   *
   * @param color Цвет фигуры
   */
  public Knight(Color color) {
    super(color);
    this.figureType = FigureType.KNIGHT;
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    var toCol = move.to().col().value();
    var toRow = move.to().row().value();
    var fromCol = move.from().col().value();
    var fromRow = move.from().row().value();

    return Figures.basicValidMove(move, board, withKingCheck)
        && ((Math.abs(fromCol - toCol) == 1 && Math.abs(fromRow - toRow) == 2)
            || (Math.abs(fromCol - toCol) == 2 && Math.abs(fromRow - toRow) == 1));
  }

  @Override
  public List<Move> getAllMoves(Board board, Position position) {
    var moveList = new ArrayList<Move>();

    moveList.add(Figures.getMoveByPositionAndDeltas(position, -2, -1));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, -2, 1));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, -1, -2));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, -1, 2));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, 1, -2));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, 1, 2));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, 2, -1));
    moveList.add(Figures.getMoveByPositionAndDeltas(position, 2, 1));

    return moveList.stream().filter(move -> canMove(board, move, true)).toList();
  }

  @Override
  public void revive(Board board, Move move) {}

  @Override
  public boolean canRevive(Board board, Move move) {
    return false;
  }
}
