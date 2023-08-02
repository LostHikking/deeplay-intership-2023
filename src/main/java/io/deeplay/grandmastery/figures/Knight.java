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
  public void move(Position from, Position to, Board board) {}

  @Override
  public boolean canMove(Position from, Position to, Board board) {
    return false;
  }

  @Override
  public List<Move> getAllMoves(Board board) {
    return null;
  }

  @Override
  public void revive(Position position, Board board, Piece piece) {}

  @Override
  public boolean canRevive(Position position, Board board) {
    return false;
  }
}
