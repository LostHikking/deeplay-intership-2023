package io.deeplay.grandmastery.algorithms;

import static io.deeplay.grandmastery.utils.Algorithms.MAX_EVAL;
import static io.deeplay.grandmastery.utils.Algorithms.MIN_EVAL;
import static io.deeplay.grandmastery.utils.Algorithms.inversColor;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.List;
import java.util.Map;

class EvaluationPrev {
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

  public static double evaluationFunc(
      Board board, GameHistory gameHistory, Color botColor, Bonuses bonuses) {
    if (GameStateChecker.isMate(board, inversColor(botColor))) {
      return MAX_EVAL;
    } else if (GameStateChecker.isMate(board, botColor)) {
      return MIN_EVAL;
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return 0;
    }

    double our_rate = evaluationBoard(board, gameHistory, botColor, bonuses);
    double opponent_rate = evaluationBoard(board, gameHistory, inversColor(botColor), bonuses);

    double result = (our_rate - opponent_rate) * (1 + 10 / (our_rate + opponent_rate)) / 1000;
    if (result < MIN_EVAL || result > MAX_EVAL) {
      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    return result;
  }

  public static double castlingBonus(Board board, GameHistory gameHistory, Bonuses bonuses) {
    Piece movedPiece = board.getPiece(board.getLastMove().to());
    if (movedPiece.getFigureType() == FigureType.KING
        || movedPiece.getFigureType() == FigureType.ROOK) {
      return bonuses.castling(movedPiece, board.getLastMove(), gameHistory) * 50.0;
    }

    return 0.0;
  }

  protected static double evaluationBoard(
      Board board, GameHistory gameHistory, Color color, Bonuses bonuses) {
    return kingEndgameEval(board, color)
        + calculatePiecesPrice(board, color)
        + castlingBonus(board, gameHistory, bonuses);
  }

  protected static double kingEndgameEval(Board board, Color color) {
    Position kingPos = board.getKingPositionByColor(color);
    Position enemyKingPos = board.getKingPositionByColor(inversColor(color));
    if (board.getAllPiecePositionByColor(color).size() != 1) {
      return 0.0;
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

  private static double calculatePawnPrice(Board board, Position pos, Color color) {
    int row = pos.row().value();
    int col = pos.col().value();

    double rowPrice =
        PIECE_PRICE.get(FigureType.PAWN) + (color == Color.WHITE ? row : 7 - row) * 0.1 - 0.1;
    double centerBonus =
        col > 2 && col < 5 && row > 2 && row < 5 ? (4 - (col % 4 + col % 3)) * 0.1 : 0;
    double doublePawnPenalty = 0;
    if (board.hasPiece(col, row + 1)
        && board.getPiece(col, row + 1).getFigureType() == FigureType.PAWN) {
      doublePawnPenalty += 0.5;
    }
    if (board.hasPiece(col, row - 1)
        && board.getPiece(col, row - 1).getFigureType() == FigureType.PAWN) {
      doublePawnPenalty += 0.5;
    }

    return rowPrice + centerBonus - doublePawnPenalty;
  }

  protected static double pieceExchange(Board board, Color color) {
    double result = 0;
    List<Position> friendlies = board.getAllPiecePositionByColor(color);
    List<Position> enemies = board.getAllPiecePositionByColor(inversColor(color));

    for (Position friendly : friendlies) {
      if (!friendly.equals(board.getKingPositionByColor(color))) {
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
      if (piece.canMove(board, move, true, false)) {
        return true;
      }
    }
    return false;
  }
}
