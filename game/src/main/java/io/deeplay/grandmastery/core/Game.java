package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.Boards;
import lombok.Getter;

@Getter
public class Game implements GameListener {
  private Color colorMove;

  private final Board board = new HashBoard();

  private boolean gameOver;

  @Override
  public void startup(Board board) {
    Boards.copyBoard(board).accept(this.board);
    colorMove = Color.WHITE;
    gameOver = false;
  }

  @Override
  public void makeMove(Move move) throws GameException {
    if (gameOver) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }

    Piece piece = board.getPiece(move.from());
    if (piece == null || colorMove != piece.getColor()) {
      throw GameErrorCode.IMPOSSIBLE_MOVE.asException();
    }

    if (piece.move(board, move)) {
      board.setLastMove(move);
    } else {
      throw GameErrorCode.IMPOSSIBLE_MOVE.asException();
    }
    colorMove = colorMove == Color.WHITE ? Color.BLACK : Color.WHITE;
  }

  @Override
  public void gameOver() {
    gameOver = true;
  }

  public void setColorMove(Color colorMove) {
    this.colorMove = colorMove;
  }

  /**
   * Метод возвращает копию текущей доски.
   *
   * @return копия текущей доски
   */
  public Board getCopyBoard() {
    Board copyBoard = new HashBoard();
    Boards.copyBoard(board).accept(copyBoard);
    return copyBoard;
  }
}
