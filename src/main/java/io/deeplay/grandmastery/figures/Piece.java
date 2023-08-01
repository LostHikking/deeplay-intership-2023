package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import java.util.List;
import java.util.Objects;

public abstract class Piece {
  protected final Color color;
  protected boolean isMoved;
  protected FigureType figureType;

  public Piece(Color color) {
    this.color = color;
    isMoved = false;
  }

  public Color getColor() {
    return color;
  }

  public boolean isMoved() {
    return isMoved;
  }

  public void setMoved() {
    isMoved = true;
  }

  public FigureType getFigureType() {
    return figureType;
  }

  public abstract void move(Position from, Position to, Board board);

  public abstract boolean canMove(Position from, Position to, Board board);

  public abstract List<Move> getAllMoves(Board board);

  public abstract void revive(Position position, Board board, Piece piece);

  public abstract boolean canRevive(Position position, Board board);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Piece piece)) {
      return false;
    }
    return isMoved == piece.isMoved && color == piece.color && figureType == piece.figureType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, isMoved, figureType);
  }
}
