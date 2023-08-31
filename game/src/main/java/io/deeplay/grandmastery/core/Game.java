package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.Boards;
import lombok.Getter;

public class Game implements GameListener {
  @Getter private GameState gameState;

  private final Board board = new HashBoard();

  @Getter private boolean gameOver;

  @Override
  public void startup(Board board) throws GameException {
    Boards.copyBoard(board).accept(this.board);
    gameState = GameState.WHITE_MOVE;
    gameOver = false;
  }

  @Override
  public void makeMove(Move move) throws GameException {
    if (gameOver) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }

    Color colorMove = gameState == GameState.WHITE_MOVE ? Color.WHITE : Color.BLACK;
    Piece piece = board.getPiece(move.from());
    if (piece == null || colorMove != piece.getColor()) {
      throw GameErrorCode.IMPOSSIBLE_MOVE.asException();
    }

    if (piece.move(board, move)) {
      board.setLastMove(move);
    } else {
      throw GameErrorCode.IMPOSSIBLE_MOVE.asException();
    }
    gameState = gameState == GameState.WHITE_MOVE ? GameState.BLACK_MOVE : GameState.WHITE_MOVE;
    board.clearMoves();
  }

  @Override
  public void gameOver(GameState gameState) {
    gameOver = true;
    this.gameState = gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
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
