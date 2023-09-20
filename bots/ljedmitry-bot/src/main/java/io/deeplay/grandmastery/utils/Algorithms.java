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
  /**
   * Создает копию истории игры и выполняет ход на доске.
   *
   * @param board Шахматная доска.
   * @param gameHistory История игры.
   * @return Новая копия истории игры с выполненным ходом.
   */
  public static GameHistory copyHistoryAndMove(Board board, GameHistory gameHistory) {
    GameHistory copyHistory = gameHistory.getCopy();
    copyHistory.addBoard(board);
    copyHistory.makeMove(board.getLastMove());

    return copyHistory;
  }

  /**
   * Создает копию доски и выполняет ход на ней.
   *
   * @param move Ход, который нужно выполнить.
   * @param board Исходная шахматная доска.
   * @return Новая копия доски с выполненным ходом.
   */
  public static Board copyAndMove(Move move, Board board) {
    Board copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);

    Piece piece = copyBoard.getPiece(move.from());
    piece.move(copyBoard, move);
    copyBoard.setLastMove(move);

    return copyBoard;
  }

  /**
   * Получает список всех возможных ходов для заданного цвета на доске.
   *
   * @param board Шахматная доска.
   * @param color Цвет (белые или черные).
   * @return Список всех возможных ходов.
   */
  public static List<Move> getPossibleMoves(Board board, Color color) {
    List<Move> moves = new ArrayList<>();
    List<Position> positions = board.getAllPiecePositionByColor(color);

    for (Position position : positions) {
      moves.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return moves;
  }

  /**
   * Проверяет, завершилась ли игра на доске.
   *
   * @param board Шахматная доска.
   * @param gameHistory История игры.
   * @return true, если игра завершена, иначе false.
   */
  public static boolean isGameOver(Board board, GameHistory gameHistory) {
    return GameStateChecker.isMate(board, Color.WHITE)
        || GameStateChecker.isMate(board, Color.BLACK)
        || GameStateChecker.isDraw(board, gameHistory);
  }
}
