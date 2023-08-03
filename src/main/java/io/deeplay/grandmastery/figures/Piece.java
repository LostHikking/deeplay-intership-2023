package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import java.util.List;
import java.util.Objects;

public abstract class Piece {
  protected char symbol;
  protected final Color color;
  protected boolean isMoved;
  protected FigureType figureType;

  public Piece(Color color) {
    this.color = color;
    isMoved = false;
  }

  public char getSymbol() {
    return symbol;
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

  /**
   * Выполняет перемещение фигуры на доске в соответствии с переданным ходом.
   *
   * @param board доска
   * @param move ход
   * @return true, если ход был выполнен успешно и фигура перемещена, false, если ход недопустим или
   *     не удалось переместить фигуру
   */
  public boolean move(Board board, Move move) {
    if (canMove(board, move)) {
      Piece piece = board.getPiece(move.from());
      board.removePiece(move.from());
      board.removePiece(move.to());
      board.setPiece(move.to(), piece);

      this.isMoved = true;
      return true;
    }

    return false;
  }

  public abstract boolean canMove(Board board, Move move);

  public abstract List<Move> getAllMoves(Board board, Position position);

  public abstract void revive(Board board, Move move);

  public abstract boolean canRevive(Board board, Move move);

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
