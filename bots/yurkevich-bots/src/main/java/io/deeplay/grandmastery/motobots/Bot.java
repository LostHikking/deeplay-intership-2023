package io.deeplay.grandmastery.motobots;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.motostrategies.Strategy;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;

public abstract class Bot extends Player {
  protected final int searchDepth;
  protected final Strategy strategy;

  protected Bot(String name, Color color, Strategy strategy, int searchDepth) {
    super(name, color);
    this.searchDepth = searchDepth;
    this.strategy = strategy;
  }

  /**
   * Метод для создания дочерних вершин.
   *
   * @param state Состояние.
   * @return Список детей.
   */
  public ArrayList<State> createChildStates(State state) {
    ArrayList<State> childStates = new ArrayList<>();
    Board parentBoard = state.getBoard();
    Piece piece;
    List<Move> moves;
    Color nextColor;
    if (state.isMainNode()) {
      nextColor = state.getMainColor();
    } else {
      nextColor = state.getMovingColor() == Color.BLACK ? Color.WHITE : Color.BLACK;
    }
    for (Position position : parentBoard.getAllPiecePositionByColor(nextColor)) {
      piece = parentBoard.getPiece(position);
      moves = piece.getAllMoves(parentBoard, position);
      for (Move move : moves) {
        Board currentBoard = new HashBoard();
        Boards.copy(parentBoard).accept(currentBoard);
        currentBoard.removePiece(move.from());
        currentBoard.setPiece(move.to(), piece);
        currentBoard.setLastMove(move);
        piece.setMoved(true);
        GameHistory tempHistory = state.getGameHistory().getCopy();
        tempHistory.addBoard(currentBoard);
        tempHistory.makeMove(move);
        State childState =
            new State(currentBoard, state.getMainColor(), nextColor, move, tempHistory, false);
        childStates.add(childState);
      }
    }
    return childStates;
  }
}
