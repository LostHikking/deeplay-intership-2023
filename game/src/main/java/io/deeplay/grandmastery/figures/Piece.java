package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameStateChecker;
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
      Piece removePiece = board.removePiece(move.to());
      board.setPiece(move.to(), piece);

      if (GameStateChecker.isCheck(board, this.color)) {
        board.removePiece(move.to());
        board.setPiece(move.from(), piece);
        if (removePiece != null) {
          board.setPiece(move.to(), removePiece);
        }

        return false;
      }

      this.isMoved = true;
      return true;
    }

    return false;
  }

  /**
   * Проверяет, может ли фигура выполнить ход на доске.
   *
   * @param board доска
   * @param move ход
   * @return true, если фигура может выполнить указанный ход, иначе false
   */
  public abstract boolean canMove(Board board, Move move, boolean withKingCheck);

  public boolean canMove(Board board, Move move) {
    return canMove(board, move, true);
  }

  /**
   * Получает все возможные ходы для фигуры с указанной позиции на доске.
   *
   * @param board доска
   * @param position позиция фигуры на доске
   * @return {@code List<Move>} список всех возможных ходов
   */
  public abstract List<Move> getAllMoves(Board board, Position position);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Piece piece)) {
      return false;
    }
    return getColor() == piece.getColor() && getFigureType() == piece.getFigureType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getColor(), getFigureType());
  }
}
