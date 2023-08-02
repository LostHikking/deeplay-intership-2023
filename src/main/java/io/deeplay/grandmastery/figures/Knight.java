package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import java.util.List;

public class Knight extends Piece {
  public Knight(Color color) {
    super(color);
    this.figureType = FigureType.KNIGHT;
  }

  @Override
  public void move(Board board, Move move) {

  }

  @Override
  public boolean canMove(Board board, Move move) {
    return true;
  }

  @Override
  public List<Move> getAllMoves(Board board, Position position) {
    return null;
  }

  @Override
  public void revive(Board board, Move move) {

  }

  @Override
  public boolean canRevive(Board board, Move move) {
    return true;
  }
}
