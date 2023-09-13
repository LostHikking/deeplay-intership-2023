package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;

class Bonuses {
  private double castling;

  public Bonuses() {
    this.castling = 0;
  }

  protected double castling(Piece movedPiece, Move move, GameHistory gameHistory) {
    if (castling > -1 && castling < 1) {
      if (movedPiece.getFigureType() == FigureType.KING) {
        if (isCastling(move)) {
          castling = 1.0;
          return castling;
        }
        castling = -1.0;
        return castling;
      } else if (movedPiece.getFigureType() == FigureType.ROOK
          && !gameHistory.getBeforeLastBoard().getPiece(move.from()).isMoved()) {
        castling -= 0.5;
        return castling;
      }
    }

    return 0.0;
  }

  protected boolean isCastling(Move move) {
    int rowFrom = move.from().row().value();
    int rowTo = move.to().row().value();
    int colFrom = move.from().col().value();
    int colTo = move.to().col().value();

    return (rowFrom == 0 || rowFrom == 7) && rowFrom == rowTo && Math.abs(colTo - colFrom) == 2;
  }
}
