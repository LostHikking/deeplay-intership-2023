package io.deeplay.grandmastery.motobots;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.motostrategies.Strategy;
import java.util.List;

public class ExpectiMaxBot extends Bot {

  /**
   * Конструктор с параметрами.
   *
   * @param name Имя
   * @param color Цвет
   * @param strategy Стратегия
   */
  public ExpectiMaxBot(String name, Color color, Strategy strategy, int searchDepth) {
    super(name, color, strategy, searchDepth);
  }

  /**
   * Алгоритм экспектимакс.
   *
   * @param state Состояние.
   * @param depth Глубина поиска.
   * @param maximizingPlayer Игрок.
   * @return Стоимость лучшего ребенка.
   */
  public int expectiMax(State state, int depth, boolean maximizingPlayer) {
    strategy.setTerminalCost(state);
    if (state.isTerminal()) {
      return state.getValue();
    } else if (depth == 0) {
      state.setValue(strategy.evaluate(state));
      return state.getValue();
    }
    List<State> children = createChildStates(state);
    if (maximizingPlayer) {
      int bestValue = Integer.MIN_VALUE;
      for (State child : children) {
        int value = expectiMax(child, depth - 1, false);

        bestValue = determineBestMove(state, child, value, bestValue);
      }
      return bestValue;
    } else {
      int averageValue = 0;
      int childCount = 0;
      for (State child : children) {
        int value = expectiMax(child, depth - 1, true);
        averageValue = averageValue + value;
        childCount = childCount + 1;
      }
      if (childCount == 0) {
        return 0;
      } else {
        state.setValue(averageValue / childCount);
        return averageValue / childCount;
      }
    }
  }

  private int determineBestMove(State state, State child, int value, int bestValue) {
    if (value > bestValue) {
      bestValue = value;
      updateNodeValueAndMove(state, child, bestValue);
    }
    return bestValue;
  }

  private void updateNodeValueAndMove(State state, State child, int bestValue) {
    state.setValue(bestValue);
    if (state.isMainNode()) {
      state.setMove(child.getMove());
    }
  }

  @Override
  public Move createMove() throws GameException {

    Board board = this.getBoard();
    Color mainColor = this.getColor();
    Color opponentColor = mainColor == Color.BLACK ? Color.WHITE : Color.BLACK;
    State mainState = new State(board, mainColor, opponentColor, null, gameHistory, true);
    expectiMax(mainState, searchDepth, true);
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
