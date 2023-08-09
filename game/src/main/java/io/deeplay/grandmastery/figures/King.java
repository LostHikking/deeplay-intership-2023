package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
  private Position rookCastlingPos;

  /**
   * Конструктор для короля.
   *
   * @param color Цвет фигуры
   */
  public King(Color color) {
    super(color);
    this.figureType = FigureType.KING;
    rookCastlingPos = null;
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    if (!Figures.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    int deltaRow = deltaRow(move);
    int deltaCol = deltaCol(move);

    if (!isMoved && deltaRow == 0 && deltaCol == 2) {
      return canCastling(board, move);
    }

    return deltaRow < 2 && deltaCol < 2;
  }

  private int deltaRow(Move move) {
    return Math.abs(move.to().row().value() - move.from().row().value());
  }

  private int deltaCol(Move move) {
    return Math.abs(move.to().col().value() - move.from().col().value());
  }

  private boolean canCastling(Board board, Move move) {
    int rookCol;
    switch (move.to().col().value()) {
      case 2 -> rookCol = 0;
      case 6 -> rookCol = 7;
      default -> {
        return false;
      }
    }

    Position rookPos = new Position(new Column(rookCol), move.from().row());
    Piece rook = board.getPiece(rookPos);
    if (rook == null || rook.isMoved || rook.color != this.color) {
      return false;
    }
    if (Figures.hasFigureOnHorizontalBetweenColPosition(
        board, move.to().row().value(), move.from().col().value(), rookCol)) {
      return false;
    }

    List<Position> positions =
        board.getAllPieceByColorPosition(this.color == Color.WHITE ? Color.BLACK : Color.WHITE);

    Move tmpMove;
    for (int i = Math.min(move.from().col().value(), move.to().col().value());
        i < Math.max(move.from().col().value(), move.to().col().value());
        i++) {
      for (Position position : positions) {
        tmpMove = new Move(position, new Position(new Column(i), move.to().row()), null);
        if (board.getPiece(position).canMove(board, tmpMove)) {
          return false;
        }
      }
    }

    rookCastlingPos = rookPos;
    return true;
  }

  @Override
  public boolean move(Board board, Move move) {
    boolean result = super.move(board, move);
    if (result && rookCastlingPos != null) {
      Piece rook = board.getPiece(rookCastlingPos);
      board.removePiece(rookCastlingPos);

      Position newRookPos;
      if (rookCastlingPos.col().value() == 0) {
        newRookPos = new Position(new Column(3), rookCastlingPos.row());
      } else {
        newRookPos = new Position(new Column(5), rookCastlingPos.row());
      }
      board.setPiece(newRookPos, rook);
    }

    return result;
  }

  @Override
  public List<Move> getAllMoves(Board board, Position position) {
    List<Move> moves = new ArrayList<>();

    moves.add(Figures.getMoveByPositionAndDeltas(position, 0, 1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 1, 1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 1, 0));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 1, -1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, 0, -1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, -1, -1));
    moves.add(Figures.getMoveByPositionAndDeltas(position, -1, 0));
    moves.add(Figures.getMoveByPositionAndDeltas(position, -1, 1));

    if (!this.isMoved) {
      if (this.color == Color.WHITE) {
        moves.add(LongAlgebraicNotation.getMoveFromString("e1c1"));
        moves.add(LongAlgebraicNotation.getMoveFromString("e1g1"));
      } else {
        moves.add(LongAlgebraicNotation.getMoveFromString("e8c8"));
        moves.add(LongAlgebraicNotation.getMoveFromString("e8g8"));
      }
    }

    return moves.stream().filter(move -> canMove(board, move)).toList();
  }
}
