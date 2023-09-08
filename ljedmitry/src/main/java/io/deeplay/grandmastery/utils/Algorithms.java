package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Algorithms {
  public static final double MAX_EVAL = 1.0;
  public static final double MIN_EVAL = -1.0;

  private static final Map<FigureType, Double> PIECE_PRICE =
      Map.of(
          FigureType.PAWN,
          1.0,
          FigureType.KNIGHT,
          3.0,
          FigureType.BISHOP,
          3.0,
          FigureType.ROOK,
          5.0,
          FigureType.QUEEN,
          9.0,
          FigureType.KING,
          100.0);

  public static GameHistory copyHistoryAndMove(Board board, GameHistory gameHistory) {
    GameHistory copyHistory = gameHistory.getCopy();
    copyHistory.addBoard(board);
    copyHistory.makeMove(board.getLastMove());

    return copyHistory;
  }

  public static Board copyAndMove(Move move, Board board) {
    Board copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);

    Piece piece = copyBoard.getPiece(move.from());
    piece.move(copyBoard, move);
    copyBoard.setLastMove(move);

    return copyBoard;
  }

  public static List<Move> getPossibleMoves(Board board, Color color) {
    List<Move> moves = new ArrayList<>();
    List<Position> positions = board.getAllPiecePositionByColor(color);

    for (Position position : positions) {
      moves.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return moves;
  }

  public static boolean isGameOver(Board board, GameHistory gameHistory) {
    return GameStateChecker.isMate(board, Color.WHITE)
        || GameStateChecker.isMate(board, Color.BLACK)
        || GameStateChecker.isDraw(board, gameHistory);
  }

  public static double evaluationFunc(Board board, GameHistory gameHistory, Color botColor) {
    if (GameStateChecker.isMate(board, inversColor(botColor))) {
      return MAX_EVAL;
    } else if (GameStateChecker.isMate(board, botColor)) {
      return MIN_EVAL;
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return 0;
    }

    double our_rate = evaluationBoard(board, botColor);
    double opponent_rate = evaluationBoard(board, inversColor(botColor));

    double result = (our_rate - opponent_rate) * (1 + 10 / (our_rate + opponent_rate)) / 1000;
    if (result < MIN_EVAL || result > MAX_EVAL) {
      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    return result;
  }

  public static double evaluationBoard(Board board, Color color) {
    return kingEndgameEval(board, color)
        + calculatePiecesPrice(board, color)
        + pieceSecurity(board, color);
  }

  protected static double kingEndgameEval(Board board, Color color) {
    Position kingPos =
        color == Color.WHITE ? board.getWhiteKingPosition() : board.getBlackKingPosition();
    Position enemyKingPos =
        color == Color.WHITE ? board.getBlackKingPosition() : board.getWhiteKingPosition();
    if (board.getAllPiecePositionByColor(color).size() > 2) {
      return 0;
    }

    int row = kingPos.row().value();
    int col = kingPos.col().value();

    int rowCenter = row <= 3 ? 3 : 4;
    int colCenter = col <= 3 ? 3 : 4;
    int rowDistForCenter = Math.abs(row - rowCenter);
    int colDistForCenter = Math.abs(col - colCenter);

    int rowDistForEnemyKing = Math.abs(row - enemyKingPos.row().value());
    int colDistForEnemyKing = Math.abs(col - enemyKingPos.col().value());

    int moveCount = board.getPiece(kingPos).getAllMoves(board, kingPos).size();

    return (rowDistForCenter + colDistForCenter) * -1
        + ((moveCount + rowDistForEnemyKing + colDistForEnemyKing) * 0.1);
  }

  protected static double calculatePiecesPrice(Board board, Color color) {
    return board.getAllPiecePositionByColor(color).stream()
        .map(pos -> calculatePiecePrice(board, pos, color))
        .reduce(0.0, Double::sum);
  }

  protected static double calculatePiecePrice(Board board, Position pos, Color color) {
    return board.getPiece(pos).getFigureType() == FigureType.PAWN
        ? calculatePawnPrice(board, pos, color)
        : PIECE_PRICE.get(board.getPiece(pos).getFigureType());
  }

  protected static double calculatePawnPrice(Board board, Position pos, Color color) {
    int row = pos.row().value();
    int col = pos.col().value();

    double rowPrice = (double) (color == Color.WHITE ? row : 7 - row) * 0.1 - 0.1;
    double centerBonus =
        col > 2 && col < 5 && row > 2 && row < 5 ? (4 - (col % 4 + col % 3)) * 0.1 : 0;
    double doublePawnPenalty = 0;
    if (board.hasPiece(col, row + 1) && board.getPiece(col, row + 1).getFigureType() == FigureType.PAWN) {
      doublePawnPenalty += 0.5;
    }
    if (board.hasPiece(col, row - 1) && board.getPiece(col, row - 1).getFigureType() == FigureType.PAWN) {
      doublePawnPenalty += 0.5;
    }

    return rowPrice + centerBonus - doublePawnPenalty;
  }

  protected static double pieceSecurity(Board board, Color color) {
    double result = 0;
    List<Position> friendlies = board.getAllPiecePositionByColor(color);
    List<Position> enemies = board.getAllPiecePositionByColor(inversColor(color));

    for (Position friendly : friendlies) {
      for (Position enemy : enemies) {
        Piece ePiece = board.getPiece(enemy);
        FigureType promotionPiece = null;
        if (ePiece.getFigureType() == FigureType.PAWN
            && (friendly.row().value() == 0 || friendly.row().value() == 7)) {
          promotionPiece = FigureType.QUEEN;
        }

        Move move = new Move(enemy, friendly, promotionPiece);
        if (ePiece.canMove(board, move)) {
          if (isSecurity(board, friendly, color)) {
            result -=
                calculatePiecePrice(board, friendly, color)
                    - calculatePiecePrice(board, enemy, inversColor(color));
          } else {
            result -= calculatePiecePrice(board, friendly, color);
          }
        }
      }
    }

    return result;
  }

  protected static boolean isSecurity(Board board, Position pos, Color color) {
    List<Position> friendlies = board.getAllPiecePositionByColor(color);

    for (Position friendly : friendlies) {
      Piece piece = board.getPiece(friendly);
      FigureType promotionPiece = null;
      if (piece.getFigureType() == FigureType.PAWN
          && (friendly.row().value() == 0 || friendly.row().value() == 7)) {
        promotionPiece = FigureType.QUEEN;
      }

      Move move = new Move(friendly, pos, promotionPiece);
      if (piece.canMove(board, move)) {
        return true;
      }
    }
    return false;
  }

  public static Color inversColor(Color color) {
    return color == Color.WHITE ? Color.BLACK : Color.WHITE;
  }
}
