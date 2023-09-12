package io.deeplay.grandmastery.figures;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.Figures;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class King extends Piece {
  private Position rookFromCastling;
  private Position rookToCastling;
  private final Position targetLeftCastling;
  private final Position targetRightCastling;

  /**
   * Конструктор для короля.
   *
   * @param color Цвет фигуры
   */
  public King(Color color) {
    super(color);
    figureType = FigureType.KING;
    rookToCastling = null;
    rookFromCastling = null;
    if (color == Color.WHITE) {
      targetLeftCastling = Position.fromString("c1");
      targetRightCastling = Position.fromString("g1");
    } else {
      targetLeftCastling = Position.fromString("c8");
      targetRightCastling = Position.fromString("g8");
    }
  }

  @Override
  public boolean canMove(Board board, Move move, boolean withKingCheck, boolean withColorCheck) {
    final int deltaRow = deltaRow(move);
    final int deltaCol = deltaCol(move);
    final int row = move.to().row().value();

    if (deltaRow == 0 && (row == 0 || row == 7)) {
      if (canCastling(board, move)) {
        return true;
      }
    }

    if (!Figures.basicValidMove(move, board, withKingCheck, withColorCheck)) {
      return false;
    }

    rookFromCastling = null;
    rookToCastling = null;
    return deltaRow < 2 && deltaCol < 2;
  }

  private int deltaRow(Move move) {
    return Math.abs(move.to().row().value() - move.from().row().value());
  }

  private int deltaCol(Move move) {
    return Math.abs(move.to().col().value() - move.from().col().value());
  }

  private boolean findRookForCastling(Board board, Move move) {
    int toCol = move.to().col().value();
    int fromCol = move.from().col().value();
    int row = move.from().row().value();
    int start;
    int end;

    if (toCol == 2) {
      start = 0;
      end = fromCol;
    } else if (toCol == 6) {
      start = fromCol;
      end = 8;
    } else {
      return false;
    }

    for (int i = start; i < end; i++) {
      Piece piece = board.getPiece(i, row);
      if (piece != null
          && piece.getFigureType() == FigureType.ROOK
          && !piece.isMoved()
          && piece.getColor() == getColor()) {
        rookFromCastling = new Position(new Column(i), new Row(row));
        return true;
      }
    }
    return false;
  }

  private boolean canCastling(Board board, Move move) {
    if (isMoved()) {
      return false;
    }

    final int toCol = move.to().col().value();
    final int row = move.from().row().value();
    final int fromCol = move.from().col().value();

    if (!findRookForCastling(board, move)) {
      return false;
    }

    if (!Figures.hasNotFigureBetweenCols(
        board,
        row,
        fromCol,
        rookFromCastling.col().value(),
        List.of(board.getPiece(rookFromCastling)))) {
      return false;
    }

    if (rookFromCastling.col().value() < fromCol) {
      rookToCastling = new Position(new Column(3), rookFromCastling.row());
    } else {
      rookToCastling = new Position(new Column(5), rookFromCastling.row());
    }

    if (!rookFromCastling.equals(rookToCastling)
        && !move.from().equals(rookToCastling)
        && board.hasPiece(rookToCastling)) {
      return false;
    }

    List<Position> positions = board.getAllPiecePositionByColor(color.getOpposite());

    for (int i = Math.min(fromCol, toCol); i < Math.max(fromCol, toCol); i++) {
      for (Position position : positions) {
        FigureType promotionPiece = null;
        if (board.getPiece(position).getFigureType() == FigureType.PAWN) {
          promotionPiece = FigureType.QUEEN;
        }

        Move tmpMove =
            new Move(position, new Position(new Column(i), move.to().row()), promotionPiece);
        if (board.getPiece(position).canMove(board, tmpMove)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public boolean move(Board board, Move move) {
    Piece left = board.getPiece(this.targetLeftCastling);
    Piece right = board.getPiece(this.targetRightCastling);

    boolean result = super.move(board, move);
    if (result && rookToCastling != null) {
      if (rookFromCastling.equals(this.targetLeftCastling)) {
        board.setPiece(rookToCastling, left);
      } else if (rookFromCastling.equals(this.targetRightCastling)) {
        board.setPiece(rookToCastling, right);
      } else {
        Piece rook = board.removePiece(rookFromCastling);
        board.setPiece(rookToCastling, rook);
      }
    }
    return result;
  }

  @Override
  protected List<Move> generateAllMoves(Board board, Position position) {
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
        realMoves.add(new Move(position, Position.fromString("c1"), null));
        realMoves.add(new Move(position, Position.fromString("g1"), null));
      } else {
        realMoves.add(new Move(position, Position.fromString("c8"), null));
        realMoves.add(new Move(position, Position.fromString("g8"), null));
      }
    }

    return realMoves.stream()
        .filter(move -> canMove(board, move) && simulationMoveAndCheck(board, move))
        .toList();
  }
}
