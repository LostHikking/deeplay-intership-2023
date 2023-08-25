package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class King extends Piece {
  private Position rookFromCastling;

  private Position rookToCastling;

  private boolean isCastling;

  /**
   * Конструктор для короля.
   *
   * @param color Цвет фигуры
   */
  public King(Color color) {
    super(color);
    this.setFigureType(FigureType.KING);
    rookToCastling = null;
    rookFromCastling = null;
    isCastling = false;
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    if (!Figures.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    final int deltaRow = deltaRow(move);
    final int deltaCol = deltaCol(move);

    if (!isMoved() && deltaRow == 0 && deltaCol == 2) {
      return canCastling(board, move);
    }

    isCastling = false;
    return deltaRow < 2 && deltaCol < 2;
  }

  private int deltaRow(Move move) {
    return Math.abs(move.to().row().value() - move.from().row().value());
  }

  private int deltaCol(Move move) {
    return Math.abs(move.to().col().value() - move.from().col().value());
  }

  private boolean canCastling(Board board, Move move) {
    final int toCol = move.to().col().value();
    final int row = move.from().row().value();
    final int fromCol = move.from().col().value();

    int rookCol;
    switch (toCol) {
      case 2 -> rookCol = 0;
      case 6 -> rookCol = 7;
      default -> {
        return false;
      }
    }

    if (row != 0 && row != 7) {
      return false;
    }

    rookFromCastling = new Position(new Column(rookCol), new Row(row));
    Piece rook = board.getPiece(rookFromCastling);
    if (rook == null || rook.isMoved() || rook.getColor() != this.getColor()) {
      return false;
    }
    if (Figures.hasFigureOnHorizontalBetweenColPosition(board, row, fromCol, rookCol)) {
      return false;
    }

    List<Position> positions =
        board.getAllPieceByColorPosition(
            this.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE);

    Move tmpMove;
    for (int i = Math.min(fromCol, toCol); i < Math.max(fromCol, toCol); i++) {
      for (Position position : positions) {
        tmpMove = new Move(position, new Position(new Column(i), move.to().row()), null);
        if (board.getPiece(position).canMove(board, tmpMove)) {
          return false;
        }
      }
    }

    if (rookFromCastling.col().value() == 0) {
      rookToCastling = new Position(new Column(3), rookFromCastling.row());
    } else {
      rookToCastling = new Position(new Column(5), rookFromCastling.row());
    }
    isCastling = true;
    return true;
  }

  @Override
  public boolean move(Board board, Move move) {
    boolean result = super.move(board, move);
    if (result && isCastling) {
      Piece rook = board.removePiece(rookFromCastling);
      board.setPiece(rookToCastling, rook);

      isCastling = false;
    }

    return result;
  }

  @Override
  public List<Move> generateAllMoves(Board board, Position position) {
    List<Move> moves = new ArrayList<>();

    moves.add(Figures.getMoveByPositionAndDeltas(position, 0, 1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 1, 1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 1, 0));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 1, -1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 0, -1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, -1, -1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, -1, 0));
    moves.add(Figures.getMoveByPositionAndDeltas(position, -1, 1));

    var realMoves = new ArrayList<>(moves.stream().filter(Objects::nonNull).toList());

    if (!this.isMoved()) {
      if (this.getColor() == Color.WHITE) {
        realMoves.add(LongAlgebraicNotation.getMoveFromString("e1c1"));
        realMoves.add(LongAlgebraicNotation.getMoveFromString("e1g1"));
      } else {
        realMoves.add(LongAlgebraicNotation.getMoveFromString("e8c8"));
        realMoves.add(LongAlgebraicNotation.getMoveFromString("e8g8"));
      }
    }

    return realMoves.stream()
        .filter(move -> canMove(board, move) && simulationMoveAndCheck(board, move))
        .toList();
  }
}
