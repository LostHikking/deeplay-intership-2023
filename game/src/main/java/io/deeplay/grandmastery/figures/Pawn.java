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
import java.util.Objects;

public class Pawn extends Piece {
  private boolean captureEnPassant;

  /**
   * Конструктор для пешки.
   *
   * @param color Цвет фигуры
   */
  public Pawn(Color color) {
    super(color);
    this.setFigureType(FigureType.PAWN);
    captureEnPassant = false;
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck) {
    if (!Figures.basicValidMove(move, board, withKingCheck)) {
      return false;
    }

    int toRow = move.to().row().value();
    captureEnPassant = false;
    if (move.promotionPiece() != null) {
      return (canMoveForward(move, board) || canCapture(move, board)) && canRevive(move);
    }

    if (toRow == 0 || toRow == 7) {
      return false;
    }

    if (move.from().col().value() == move.to().col().value()) {
      return canMoveForward(move, board);
    } else {
      if (board.getLastMove() != null && !board.hasPiece(move.to())) {
        return checkCaptureEnPassant(move, board);
      }
      return canCapture(move, board);
    }
  }

  private boolean canMoveForward(Move move, Board board) {
    int deltaRow = deltaRowByColor(move, this.getColor());

    if (deltaRow == 1 && board.getPiece(move.to()) == null) {
      return true;
    } else {
      return deltaRow == 2
          && !isMoved()
          && !board.hasPiece(move.to())
          && !board.hasPiece(
              move.from().col().value(), (move.from().row().value() + move.to().row().value()) / 2);
    }
  }

  private boolean canCapture(Move move, Board board) {
    if (deltaCol(move) == 1
        && deltaRowByColor(move, this.getColor()) == 1
        && board.hasPiece(move.to())) {
      return board.getPiece(move.to()).getColor() != this.getColor();
    } else {
      return false;
    }
  }

  private boolean checkCaptureEnPassant(Move move, Board board) {
    Move lastMove = board.getLastMove();
    int lastDeltaRow = deltaRowByColor(lastMove, board.getPiece(lastMove.to()).getColor());
    FigureType figure = board.getPiece(lastMove.to()).getFigureType();

    int deltaCol = Math.abs(move.from().col().value() - lastMove.to().col().value());
    int deltaRow = move.from().row().value() - lastMove.to().row().value();
    int colEnemyPiece = lastMove.to().col().value();
    int toCol = move.to().col().value();

    if (deltaCol == 1
        && deltaRow == 0
        && lastDeltaRow == 2
        && figure == FigureType.PAWN
        && colEnemyPiece == toCol) {
      this.captureEnPassant = deltaRowByColor(move, this.getColor()) == 1;
    }

    return captureEnPassant;
  }

  private int deltaCol(Move move) {
    return Math.abs(move.from().col().value() - move.to().col().value());
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

    if (this.captureEnPassant && result) {
      board.removePiece(board.getLastMove().to());
    }

    this.captureEnPassant = false;
    return result;
  }

  @Override
  protected boolean simulationMoveAndCheck(Board board, Move move) {
    Piece removePiece = null;
    if (this.captureEnPassant) {
      removePiece = board.removePiece(board.getLastMove().to());
    }

    boolean isCheck = !super.simulationMoveAndCheck(board, move);
    if (removePiece != null) {
      board.setPiece(board.getLastMove().to(), removePiece);
    }

    return !isCheck;
  }

  @Override
  public List<Move> generateAllMoves(Board board, Position position) {
    List<Move> defaultMoves = new ArrayList<>();

    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, 1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, -1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, 2));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 0, -2));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 1, 1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, -1, 1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, 1, -1));
    defaultMoves.add(Figures.getMoveByPositionAndDeltas(position, -1, -1));

    var realMoves = defaultMoves.stream().filter(Objects::nonNull).toList();

    List<Move> resultMoves = new ArrayList<>();
    for (Move move : realMoves) {
      if (this.getColor() == Color.WHITE) {
        if (move.to().row().value() == 7) {
          generateReviveMoves(resultMoves, move);
        } else {
          resultMoves.add(move);
        }
      } else if (this.getColor() == Color.BLACK) {
        if (move.to().row().value() == 0) {
          generateReviveMoves(resultMoves, move);
        } else {
          resultMoves.add(move);
        }
      }
    }

    resultMoves =
        resultMoves.stream()
            .filter(move -> canMove(board, move) && simulationMoveAndCheck(board, move))
            .toList();
    return resultMoves;
  }

  private void generateReviveMoves(List<Move> moves, Move move) {
    moves.add(new Move(move.from(), move.to(), FigureType.BISHOP));
    moves.add(new Move(move.from(), move.to(), FigureType.ROOK));
    moves.add(new Move(move.from(), move.to(), FigureType.QUEEN));
    moves.add(new Move(move.from(), move.to(), FigureType.KNIGHT));
  }

  /**
   * Превращение пешки.
   *
   * @param board доска
   * @param move ход
   */
  private void revive(Board board, Move move) {
    board.removePiece(move.to());

    switch (move.promotionPiece()) {
      case ROOK -> board.setPiece(move.to(), new Rook(this.getColor()));
      case QUEEN -> board.setPiece(move.to(), new Queen(this.getColor()));
      case KNIGHT -> board.setPiece(move.to(), new Knight(this.getColor()));
      case BISHOP -> board.setPiece(move.to(), new Bishop(this.getColor()));
      default -> throw GameErrorCode.IMPOSSIBLE_PAWN_REVIVE.asException();
    }
  }

  /**
   * Проверяет, может ли пешка превратится.
   *
   * @param move ход
   * @return true, если пешка может быть превращена, иначе false
   */
  private boolean canRevive(Move move) {
    if (move.promotionPiece() != FigureType.PAWN && move.promotionPiece() != FigureType.KING) {
      return this.getColor() == Color.WHITE
          ? move.to().row().value() == 7
          : move.to().row().value() == 0;
    }

    return false;
  }
}
