package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;

/** Класс для сохранения истории партии. */
public class GameHistory implements GameListener {
  private final List<Move> moves = new ArrayList<>();
  private int movesWithoutTakingAndAdvancingPawns = 0;
  private boolean gameOver;
  private final List<Board> boards = new ArrayList<>();

  /**
   * * Метод устанавливает начальное состояние доски.
   *
   * @param board Какая-то реализация доски
   */
  @Override
  public void startup(Board board) {
    addBoard(board);
    gameOver = false;
  }

  /**
   * * Метод записывает ход в историю.
   *
   * @param move Ход
   */
  @Override
  public void makeMove(Move move) {
    if (gameOver) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }
    movesWithoutTakingAndAdvancingPawns++;

    Board board = getCurBoard();
    Board beforeLast = getBeforeLastBoard();
    var toPos = move.to();
    var piece = board.getPiece(toPos);
    Piece pieceToBeforeMove = beforeLast != null ? beforeLast.getPiece(toPos) : null;

    if (piece.getFigureType() == FigureType.PAWN
        || move.promotionPiece() != null
        || pieceToBeforeMove != null) {
      movesWithoutTakingAndAdvancingPawns = 0;
    }
    moves.add(move);
  }

  @Override
  public void gameOver() {
    this.gameOver = true;
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
   * * Метод возвращает текущую доску.
   *
   * @return Доску
   */
  public Board getCurBoard() {
    return boards.isEmpty() ? null : boards.get(boards.size() - 1);
  }

  /**
   * * Метод возвращает позапрошлую доску.
   *
   * @return Доску
   */
  public Board getBeforeLastBoard() {
    return boards.size() > 1 ? boards.get(boards.size() - 2) : null;
  }

  /**
   * Метод копирует и добавляет новую борду в историю. По идее добавление должно происходить перед
   * каждым makeMove в GameHistory
   *
   * @param board доска для копирования.
   */
  public void addBoard(Board board) {
    Board copyBoard = new HashBoard();
    Boards.copyBoard(board).accept(copyBoard);
    boards.add(board);
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
                  List<Position> allOldPosition = historyBoard.getAllPiecePosition();

                  for (Position position : allOldPosition) {
                    Piece historyBoardPiece = historyBoard.getPiece(position);
                    Piece boardPiece = checkBoard.getPiece(position);
                    if (historyBoardPiece != null && !historyBoardPiece.equals(boardPiece)) {
                      return false;
                    } else if (boardPiece != null && !boardPiece.equals(historyBoardPiece)) {
                      return false;
                    }
                  }

                  return true;
                })
            .count();
  }
}
