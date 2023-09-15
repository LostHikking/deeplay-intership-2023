package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Figures;
import java.util.List;

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
          && gameHistory.getBoards().size() > 1
          && !gameHistory.getBeforeLastBoard().getPiece(move.from()).isMoved()) {
        castling -= 0.5;
        return castling;
      }
    }

    return 0.0;
  }

  private boolean isCastling(Move move) {
    int rowFrom = move.from().row().value();
    int rowTo = move.to().row().value();
    int colFrom = move.from().col().value();
    int colTo = move.to().col().value();

    return (rowFrom == 0 || rowFrom == 7) && rowFrom == rowTo && Math.abs(colTo - colFrom) == 2;
  }

  protected double middlegame(Board board, GameHistory gameHistory, Color color) {
    double result = 0.0;
    if (gameHistory.getMoves().size() > 13) {
      List<Position> friendlies = board.getAllPiecePositionByColor(color);

      for (Position friendly : friendlies) {
        Piece piece = board.getPiece(friendly);
        if ((piece.getFigureType() == FigureType.KNIGHT
            || piece.getFigureType() == FigureType.ROOK
            || piece.getFigureType() == FigureType.BISHOP)) {
          int beginRow = color == Color.WHITE ? 0 : 7;
          result -= friendly.row().value() == beginRow ? 1 : 0;
        }
      }
    }
    return result;
  }

  protected double openLines(Board board, Color color, int countMove) {
    double result = 0.0;
    if (countMove < 10) {
      return result;
    }

    List<Position> friendlies = board.getAllPiecePositionByColor(color);
    for (Position friendly : friendlies) {
      Piece piece = board.getPiece(friendly);
      if (piece.getFigureType() == FigureType.ROOK) {
        result += openVerticalAndHorizontalLines(board, color, friendly);
      } else if (piece.getFigureType() == FigureType.QUEEN) {
        result += openVerticalAndHorizontalLines(board, color, friendly);
      }
    }
    return result;
  }

  private double openVerticalAndHorizontalLines(Board board, Color color, Position pos) {
    int row = pos.row().value();
    int col = pos.col().value();
    boolean openHorizontal = Figures.hasNotFigureBetweenCols(board, row, -1, 8, List.of(pos));
    boolean openVertical = Figures.hasNotFigureBetweenRows(board, col, -1, 8, List.of(pos));
    double result = 0.0;

    if (openVertical) {
      result += col == 3 || col == 4 ? 0.5 : 0.25;
    }

    if (openHorizontal) {
      int endRow = color == Color.WHITE ? 7 : 0;
      result += row == endRow || row == Math.abs(endRow - 1) ? 0.5 : 0.25;
    }

    return result;
  }
}
