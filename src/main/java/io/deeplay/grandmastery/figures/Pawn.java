package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import java.util.List;

public class Pawn extends Piece {
  public Pawn(Color color) {
    super(color);
    this.figureType = FigureType.PAWN;
    if(color==Color.WHITE)
    {
      this.symbol='\u2659';
    }
    else
    {
      this.symbol='\u265F';
    }
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
