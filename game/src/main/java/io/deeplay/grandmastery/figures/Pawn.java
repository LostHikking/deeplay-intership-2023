package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.utils.Figures;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
  private boolean captureEnPassant;

  /**
   * Конструктор для пешки.
   *
   * @param color Цвет фигуры
   */
  public Pawn(Color color) {
    super(color);
    this.figureType = FigureType.PAWN;
    captureEnPassant = false;

    if (color != Color.WHITE) {
      this.symbol = '♙';
    } else {
      this.symbol = '♟';
    }
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    if (!Figures.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    if (move.promotionPiece() != null) {
      return (canMoveForward(move, board) || canCapture(move, board)) && canRevive(board, move);
    }

    if (move.from().col().value() == move.to().col().value()) {
      return canMoveForward(move, board);
    } else if (areOnDiagonal(move)) {
      if (board.getLastMove() != null) {
        checkCaptureEnPassant(move, board);
      }
      return captureEnPassant || canCapture(move, board);
    }
    return false;
  }

  private boolean canMoveForward(Move move, Board board) {
    int deltaRow = deltaRowByColor(move, this.color);

    if (deltaRow == 1 && board.getPiece(move.to()) == null) {
      return true;
    } else {
      return deltaRow == 2
          && !isMoved
          && !board.hasPiece(move.to())
          && !board.hasPiece(
              move.from().col().value(), (move.from().row().value() + move.to().row().value()) / 2);
    }
  }

  private boolean areOnDiagonal(Move move) {
    int diffRow = Math.abs(move.to().row().value() - move.from().row().value());
    int diffCol = Math.abs(move.to().col().value() - move.from().col().value());
    return diffCol == diffRow;
  }

  private boolean canCapture(Move move, Board board) {
    if (deltaRowByColor(move, this.color) == 1 && board.hasPiece(move.to())) {
      return board.getPiece(move.to()).getColor() != this.color;
    } else {
      return false;
    }
  }

  private void checkCaptureEnPassant(Move move, Board board) {
    int d = deltaRowByColor(board.getLastMove(), board.getPiece(board.getLastMove().to()).color);

    if (d == 2 && board.getPiece(board.getLastMove().to()).figureType == FigureType.PAWN) {
      this.captureEnPassant =
          Math.abs(deltaRowByColor(move, this.color)) == 1 && !board.hasPiece(move.to());
    }
  }

  private int deltaRowByColor(Move move, Color color) {
    int rowFrom = move.from().row().value();
    int rowTo = move.to().row().value();
    return color == Color.WHITE ? rowTo - rowFrom : rowFrom - rowTo;
  }

  @Override
  public boolean move(Board board, Move move) {
    boolean result = super.move(board, move);

    if (move.promotionPiece() != null && result) {
      revive(board, move);
      return true;
    }

    if (this.captureEnPassant) {
      board.removePiece(board.getLastMove().to());
      this.captureEnPassant = false;
    }

    return result;
  }

  @Override
  public List<Move> getAllMoves(Board board, Position position) {
    List<Move> defaultMoves = new ArrayList<>();

    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, 1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, -1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, 2));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, -2));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 1, 1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, -1, 1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 1, -1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, -1, -1));
    defaultMoves = defaultMoves.stream().filter(move -> canMove(board, move, true)).toList();

    List<Move> resultMoves = new ArrayList<>();
    for (Move move : defaultMoves) {
      if (this.color == Color.WHITE) {
        if (move.to().row().value() == 7) {
          generateReviveMoves(resultMoves, move);
        } else {
          resultMoves.add(move);
        }
      } else if (this.color == Color.BLACK) {
        if (move.to().row().value() == 0) {
          generateReviveMoves(resultMoves, move);
        } else {
          resultMoves.add(move);
        }
      }
    }

    return resultMoves;
  }

  private void generateReviveMoves(List<Move> moves, Move move) {
    moves.add(new Move(move.from(), move.to(), FigureType.BISHOP));
    moves.add(new Move(move.from(), move.to(), FigureType.ROOK));
    moves.add(new Move(move.from(), move.to(), FigureType.QUEEN));
    moves.add(new Move(move.from(), move.to(), FigureType.KNIGHT));
  }

  @Override
  public void revive(Board board, Move move) {
    board.removePiece(move.to());
    switch (move.promotionPiece()) {
      case ROOK -> board.setPiece(move.to(), new Rook(this.color));
      case QUEEN -> board.setPiece(move.to(), new Queen(this.color));
      case KNIGHT -> board.setPiece(move.to(), new Knight(this.color));
      case BISHOP -> board.setPiece(move.to(), new Bishop(this.color));
      default -> throw GameErrorCode.IMPOSSIBLE_PAWN_REVIVE.asException();
    }
  }

  @Override
  public boolean canRevive(Board board, Move move) {
    if (move.promotionPiece() != FigureType.PAWN && move.promotionPiece() != FigureType.KING) {
      return this.color == Color.WHITE
          ? move.to().row().value() == 7
          : move.to().row().value() == 0;
    }

    return false;
  }
}
