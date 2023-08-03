package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import java.util.List;

public class Pawn extends Piece {
  /**
   * Конструктор для пешки.
   *
   * @param color Цвет фигуры
   */
  public Pawn(Color color) {
    super(color);
    this.figureType = FigureType.PAWN;
    if (color == Color.WHITE) {
      this.symbol = '♙';
    } else {
      this.symbol = '♟';
    }
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
