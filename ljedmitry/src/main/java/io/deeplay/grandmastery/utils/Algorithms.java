package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.List;

public class Algorithms {
  public static final double MAX_EVAL = 1.0;
  public static final double MIN_EVAL = -1.0;

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

  public static Color inversColor(Color color) {
    return color == Color.WHITE ? Color.BLACK : Color.WHITE;
  }
}
