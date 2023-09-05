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

  private static final Map<Integer, Integer> PAWN_PRICE =
      Map.of(1, 1, 2, 1, 3, 2, 4, 3, 5, 5, 6, 8);
  private static final Map<FigureType, Integer> PIECE_PRICE =
      Map.of(
          FigureType.KNIGHT,
          288,
          FigureType.BISHOP,
          345,
          FigureType.ROOK,
          480,
          FigureType.QUEEN,
          1077,
          FigureType.KING,
          0);

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

  public static double evaluationBoard(Board board, GameHistory gameHistory, Color mainColor) {
    if (GameStateChecker.isMate(board, inversColor(mainColor))) {
      return MAX_EVAL;
    } else if (GameStateChecker.isMate(board, mainColor)) {
      return MIN_EVAL;
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return 0;
    }

    return evaluationFunc(board, mainColor);
  }

  public static double evaluationFunc(Board board, Color color) {
    int sumFigurePrice =
        board.getAllPiecePositionByColor(color).stream()
            .map(
                pos ->
                    board.getPiece(pos).getFigureType() == FigureType.PAWN
                        ? PAWN_PRICE.get(pos.row().value())
                        : PIECE_PRICE.get(board.getPiece(pos).getFigureType()))
            .reduce(0, Integer::sum);

    return (double) sumFigurePrice / Math.pow(10, countDigit(sumFigurePrice));
  }

  private static int countDigit(int number) {
    int count = 0;
    while (number != 0) {
      number /= 10;
      count++;
    }

    return count;
  }

  public static Color inversColor(Color color) {
    return color == Color.WHITE ? Color.BLACK : Color.WHITE;
  }
}
