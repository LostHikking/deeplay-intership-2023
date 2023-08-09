package io.deeplay.grandmastery.core;

import static io.deeplay.grandmastery.domain.GameState.BLACK_MOVE;
import static io.deeplay.grandmastery.domain.GameState.BLACK_WIN;
import static io.deeplay.grandmastery.domain.GameState.IMPOSSIBLE_MOVE;
import static io.deeplay.grandmastery.domain.GameState.INIT_STATE;
import static io.deeplay.grandmastery.domain.GameState.ROLLBACK;
import static io.deeplay.grandmastery.domain.GameState.STALEMATE;
import static io.deeplay.grandmastery.domain.GameState.WHITE_MOVE;
import static io.deeplay.grandmastery.domain.GameState.WHITE_WIN;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Boards;

public class Game implements GameListener {
  private GameState gameState;

  private GameState prevGameState;

  private GameHistoryListener gameHistoryListener;

  private StateListener stateListener;

  public void setGameHistoryListener(GameHistoryListener gameHistoryListener) {
    this.gameHistoryListener = gameHistoryListener;
  }

  public void setStateListener(StateListener stateListener) {
    this.stateListener = stateListener;
  }

  private boolean notifyCheckIsDraw(Board board) {
    return gameHistoryListener.checkIsDraw(board);
  }

  private void notifyStateListeners() {
    stateListener.changeGameState(gameState);
  }

  @Override
  public void startup(Board board) {
    gameState = INIT_STATE;
    Boards.defaultChess().accept(board);
    gameState = WHITE_MOVE;
    prevGameState = WHITE_MOVE;
    //    notifyStateListeners();
  }

  @Override
  public void makeMove(Move move, Board board) {
    Piece piece = board.getPiece(move.from());
    if (piece == null) {
      gameState = IMPOSSIBLE_MOVE;
      return;
    }

    if (piece.move(board, move)) {
      board.setLastMove(move);
    } else {
      gameState = IMPOSSIBLE_MOVE;
      return;
    }

    Color enemyColor = (gameState == GameState.WHITE_MOVE) ? Color.BLACK : Color.WHITE;
    Color currentColor = (gameState == GameState.WHITE_MOVE) ? Color.WHITE : Color.BLACK;

    if (GameStateChecker.isCheck(board, currentColor)) {
      prevGameState = gameState;
      gameState = ROLLBACK;
    } else if (GameStateChecker.isMate(board, enemyColor)) {
      gameState = currentColor == Color.WHITE ? WHITE_WIN : BLACK_WIN;
    } else if (notifyCheckIsDraw(board)) {
      gameState = STALEMATE;
    } else {
      gameState = currentColor == Color.WHITE ? BLACK_MOVE : WHITE_MOVE;
    }
  }

  @Override
  public void rollback(Board board) {
    gameState = prevGameState;
  }

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public GameState getPrevGameState() {
    return prevGameState;
  }
}
