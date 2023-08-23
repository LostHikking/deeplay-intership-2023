package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/** Класс для сохранения истории партии. */
@Getter
public class GameHistory implements GameListener {
  private final List<Move> moves = new ArrayList<>();

  private int movesWithoutTakingAndAdvancingPawns = 0;

  private boolean gameOver;

  private final List<Board> boards = new ArrayList<>();

  private GameState resultGame;

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
  public void gameOver(GameState gameState) {
    this.gameOver = true;
    resultGame = gameState;
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
    boards.add(copyBoard);
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
            .filter(historyBoard -> Boards.isEqualsBoards(historyBoard, checkBoard))
            .count();
  }
}
