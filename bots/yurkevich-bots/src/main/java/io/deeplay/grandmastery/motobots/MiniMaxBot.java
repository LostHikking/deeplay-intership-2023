package io.deeplay.grandmastery.motobots;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.motostrategies.Strategy;
import java.util.List;

public class MiniMaxBot extends Bot {

  /**
   * Конструктор с параметрами.
   *
   * @param name Имя.
   * @param color Цвет.
   * @param strategy Стратегия.
   */
  public MiniMaxBot(String name, Color color, Strategy strategy, int searchDepth) {
    super(name, color, strategy, searchDepth);
  }

  /**
   * Алгоритм минимакс.
   *
   * @param state Вершина.
   * @param depth глубина поиска.
   * @param maximizingPlayer Игрок, совершающий ход.
   * @param alpha Альфа значение.
   * @param beta Бета значение.
   * @return Лучшее значение.
   */
  public int minimax(State state, int depth, boolean maximizingPlayer, int alpha, int beta) {
    strategy.setTerminalCost(state);
    if (state.isTerminal()) {
      return state.getValue();
    } else if (depth == 0) {
      state.setValue(strategy.evaluate(state));
      return state.getValue();
    }

    int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    List<State> children = createChildStates(state);
    state.setChildren(children);
    for (State child : children) {
      int value = minimax(child, depth - 1, !maximizingPlayer, alpha, beta);

      bestValue = determineBestMove(state, child, value, bestValue, maximizingPlayer);

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
      State node, State child, int value, int bestValue, boolean maximizingPlayer) {
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
  private void updateNodeValueAndMove(State node, State child, int bestValue) {
    node.setValue(bestValue);
    if (node.isMainNode()) {
      node.setMove(child.getMove());
    }
  }

  @Override
  public Move createMove() throws GameException {
    Board board = this.getBoard();
    Color mainColor = this.getColor();
    Color opponentColor = mainColor == Color.BLACK ? Color.WHITE : Color.BLACK;
    State mainState = new State(board, mainColor, opponentColor, null, gameHistory, true);
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
