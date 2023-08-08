package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;

/** Класс для сохранения истории партии. */
public class GameHistory implements GameListener, GameHistoryListener {
  private final List<Move> moves = new ArrayList<>();
  private Board board;
  private int movesWithoutTakingAndAdvancingPawns = 0;

  private final List<Board> boards = new ArrayList<>();

  /**
   * * Метод устанавливает начальное состояние доски.
   *
   * @param board Какая-то реализация доски
   */
  @Override
  public void startup(Board board) {
    if (this.board == null) {
      var copyBoard = new HashBoard();
      Boards.copyBoard(board).accept(copyBoard);
      this.board = copyBoard;
    }
  }

  /**
   * * Метод записывает ход в историю.
   *
   * @param move Ход
   */
  @Override
  public void makeMove(Move move, Board board) {
    movesWithoutTakingAndAdvancingPawns++;

    var toPos = move.to();
    var piece = board.getPiece(toPos);
    Piece pieceToBeforeMove =
        !boards.isEmpty() ? boards.get(boards.size() - 1).getPiece(toPos) : null;

    if (piece.getFigureType() == FigureType.PAWN
        || move.promotionPiece() != null
        || pieceToBeforeMove != null) {
      movesWithoutTakingAndAdvancingPawns = 0;
    }

    var newBoard = new HashBoard();
    Boards.copyBoard(board).accept(newBoard);

    boards.add(newBoard);
    moves.add(move);
  }

  /**
   * Метод возвращает пуста ли история в данный момент.
   *
   * @return Пуста ли история
   */
  public boolean isEmpty() {
    return moves.isEmpty();
  }

  /**
   * * Метод возвращает последний сделанный в партии ход.
   *
   * @return последний ход
   */
  public Move getLastMove() {
    if (this.isEmpty()) {
      throw GameErrorCode.MOVE_NOT_FOUND.asException();
    }

    return moves.get(moves.size() - 1);
  }

  /**
   * * Метод возвращает все ходы в партии.
   *
   * @return Все ходы партии
   */
  public List<Move> getMoves() {
    return moves;
  }

  /**
   * * Метод возвращает доску.
   *
   * @return Доску
   */
  public Board getBoard() {
    if (board == null) {
      return null;
    }

    var copyBoard = new HashBoard();
    Boards.copyBoard(board).accept(copyBoard);
    return copyBoard;
  }

  public Board getBoardByIndex(int index) {
    return boards.get(index);
  }

  /** Удаляет последнее состояние доски из списка состояний, если список состояний не пуст. */
  public void removeLastStateBoard() {
    if (!boards.isEmpty()) {
      boards.remove(boards.size() - 1);
    }
  }

  public Move getMoveByIndex(int index) {
    return moves.get(index);
  }

  public void removeLastMove() {
    moves.remove(moves.size() - 1);
  }

  @Override
  public void rollback(Board board) {
    Boards.copyBoard(getBoardByIndex(boards.size() - 2)).accept(board);
    board.setLastMove(getMoveByIndex(moves.size() - 2));

    removeLastStateBoard();
    removeLastMove();
  }

  /**
   * Метод возвращает количество ходов без взятий и продвижения пешек.
   *
   * @return Количество ходов без взятий и продвижения пешек
   */
  public int getMovesWithoutTakingAndAdvancingPawns() {
    return movesWithoutTakingAndAdvancingPawns;
  }

  /**
   * Метод возвращает количество повторений позиции на доске в истории.
   *
   * @param checkBoard Доска
   * @return Количество повторений позиции на доске в истории
   */
  public int getMaxRepeatPosition(Board checkBoard) {
    return (int)
        boards.stream()
            .filter(
                historyBoard -> {
                  for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                      var historyBoardPiece = historyBoard.getPiece(i, j);
                      var boardPiece = checkBoard.getPiece(i, j);

                      if (historyBoardPiece != null && !historyBoardPiece.equals(boardPiece)) {
                        return false;
                      } else if (boardPiece != null && !boardPiece.equals(historyBoardPiece)) {
                        return false;
                      }
                    }
                  }

                  return true;
                })
            .count();
  }

  @Override
  public boolean checkIsDraw(Board board) {
    return GameStateChecker.isDraw(board, this);
  }
}
