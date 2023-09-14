package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;

public class MiniMaxBot extends Player {
  private int searchDepth;

  /**
   * Конструктор с параметрами.
   *
   * @param name Имя
   * @param color Цвет
   */
  public MiniMaxBot(String name, Color color, int searchDepth) {
    super(name, color);
    this.searchDepth = searchDepth;
  }

  /**
   * Метод для создания дочерних вершин.
   *
   * @param state Состояние.
   * @return Список детей.
   */
  public ArrayList<StateNode> createChildNodes(StateNode state) {
    ArrayList<StateNode> stateNodes = new ArrayList<>();
    Board parentBoard = state.getBoard();
    Piece piece;
    List<Move> moves;
    Color nextColor;
    if (state.isMainNode()) {
      nextColor = state.getMainColor();
    } else {
      nextColor = state.getMovingColor() == Color.BLACK ? Color.WHITE : Color.BLACK;
    }
    for (Position position : parentBoard.getAllPiecePositionByColor(state.getMovingColor())) {
      piece = parentBoard.getPiece(position);
      moves = piece.getAllMoves(parentBoard, position);
      for (Move move : moves) {
        Board currentBoard = new HashBoard();
        Boards.copy(parentBoard).accept(currentBoard);
        currentBoard.removePiece(move.from());
        currentBoard.setPiece(move.to(), piece);
        currentBoard.setLastMove(move);
        piece.setMoved(true);
        GameHistory tempHistory = gameHistory.getCopy();
        tempHistory.addBoard(currentBoard);
        tempHistory.makeMove(move);
        StateNode childState =
            new StateNode(currentBoard, state.getMainColor(), nextColor, move, tempHistory, false);
        stateNodes.add(childState);
      }
    }
    return stateNodes;
  }

  /**
   * Алгоритм минимакс.
   *
   * @param node Вершина.
   * @param depth глубина поиска.
   * @param maximizingPlayer Игрок, совершающий ход.
   * @param alpha Альфа значение.
   * @param beta Бета значение.
   * @return Лучшее значение.
   */
  public int minimax(StateNode node, int depth, boolean maximizingPlayer, int alpha, int beta) {
    node.setCost();

    if (node.isTerminal() || depth == 0) {
      return node.getValue();
    }

    int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    List<StateNode> children = createChildNodes(node);
    node.setChildren(children);
    for (StateNode child : children) {
      int value = minimax(child, depth - 1, !maximizingPlayer, alpha, beta);

      bestValue = determineBestMove(node, child, value, bestValue, maximizingPlayer);

      if (maximizingPlayer) {
        alpha = Math.max(alpha, bestValue);
      } else {
        beta = Math.min(beta, bestValue);
      }
      if (beta <= alpha) {
        break;
      }
    }
    return bestValue;
  }

  /**
   * Ищет лучшее значение в зависимости от игрока, совершающего ход.
   *
   * @param node Вершина.
   * @param child Дети.
   * @param value Само значение.
   * @param bestValue Лучшее значение.
   * @param maximizingPlayer Игрок, совершающий ход.
   * @return лучшее значение.
   */
  private int determineBestMove(
      StateNode node, StateNode child, int value, int bestValue, boolean maximizingPlayer) {
    if (maximizingPlayer && value > bestValue) {
      bestValue = value;
      updateNodeValueAndMove(node, child, bestValue);
    } else if (!maximizingPlayer && value < bestValue) {
      bestValue = value;
      updateNodeValueAndMove(node, child, bestValue);
    }
    return bestValue;
  }

  /**
   * Обновляет значение и ход.
   *
   * @param node Вершина.
   * @param child Дочерняя вершина.
   * @param bestValue Значение.
   */
  private void updateNodeValueAndMove(StateNode node, StateNode child, int bestValue) {
    node.setValue(bestValue);
    if (node.isMainNode()) {
      node.setMove(child.getMove());
    }
  }

  @Override
  public Move createMove() throws GameException {
    Board board = this.getBoard();
    Color mainColor = this.getColor();
    StateNode mainState = new StateNode(board, mainColor, mainColor, null, gameHistory, true);
    minimax(mainState, searchDepth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
    Move move = mainState.getMove();
    if (move.promotionPiece() != null) {
      move = new Move(move.from(), move.to(), FigureType.QUEEN);
    }
    this.lastMove = move;
    return move;
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }
}
