package io.deeplay.grandmastery.algorithms;

import static io.deeplay.grandmastery.utils.Algorithms.inversColor;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.Map;

class Evaluation {
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

  public static double evaluationFunc(
      Board board,
      GameHistory gameHistory,
      Color botColor,
      Bonuses ourBonuses,
      Bonuses opponentBonuses) {
    if (GameStateChecker.isMate(board, inversColor(botColor))) {
      return MAX_EVAL;
    } else if (GameStateChecker.isMate(board, botColor)) {
      return MIN_EVAL;
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return 0;
    }

    double ourRate = evaluationBoard(board, gameHistory, botColor, ourBonuses);
    double opponentRate =
        evaluationBoard(board, gameHistory, inversColor(botColor), opponentBonuses);

    return (ourRate - opponentRate) * (1 + 10 / (ourRate + opponentRate)) / 1000;
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
}
