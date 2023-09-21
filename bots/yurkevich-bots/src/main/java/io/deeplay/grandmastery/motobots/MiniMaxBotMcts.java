package io.deeplay.grandmastery.motobots;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.motostrategies.Strategy;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;

/** Минимакс бот с алгоритмом Монте-Карло. */
public class MiniMaxBotMcts extends Bot {
  public MiniMaxBotMcts(String name, Color color, Strategy strategy, int searchDepth) {
    super(name, color, strategy, searchDepth);
  }

  /**
   * Метод, запускающий определенное количество рандомных симуляций.
   *
   * @param board Доска.
   * @param mainColor Цвет фигуры последнего хода.
   * @param numSimulations Количество симуляций.
   * @return Цена состояния(на основе статистики).
   */
  public int runSimulations(Board board, Color mainColor, int numSimulations) {
    int win = 0;
    int draw = 0;
    int simulationResult;
    int result;
    for (int i = 0; i < numSimulations; i++) {
      simulationResult = simulation(board, mainColor);
      if (simulationResult > 0) {
        win++;
      } else if (simulationResult == 0) {
        draw++;
      }
    }
    result = (int) (((win + 0.5 * draw) / numSimulations) * 2000);
    return result;
  }

  /**
   * Метод, запускающий рандомную симуляцию.
   *
   * @param board Доска.
   * @param mainColor Цвет последнего хода.
   * @return Результат игры.
   */
  public int simulation(Board board, Color mainColor) {
    Color movingColor = mainColor == Color.WHITE ? Color.BLACK : Color.WHITE;
    Color opponentColor = mainColor == Color.WHITE ? Color.BLACK : Color.WHITE;
    Board simulationBoard = new HashBoard();
    Boards.copy(board).accept(simulationBoard);
    simulationBoard.clearMoves();

    boolean isMateForMain = false;
    boolean isMateForOpponent = false;
    boolean isDraw = false;
    int moves = 0;
    int result = 0;
    int movesLimit = 50;
    Piece piece;
    Move move;
    do {
      move = simulationRandomMove(simulationBoard, movingColor);
      if (move == null) {
        break;
      }
      simulationBoard.clearMoves();
      piece = simulationBoard.getPiece(move.from());
      simulationBoard.removePiece(move.from());
      simulationBoard.setPiece(move.to(), piece);
      simulationBoard.setLastMove(move);
      piece.setMoved(true);
      isMateForMain = GameStateChecker.isMate(simulationBoard, mainColor);
      isMateForOpponent = GameStateChecker.isMate(simulationBoard, opponentColor);
      movingColor = movingColor == Color.WHITE ? Color.BLACK : Color.WHITE;
      moves++;
    } while (!isMateForMain && !isMateForOpponent && !isDraw && moves < movesLimit);
    if (isMateForMain) {
      result = -1;
    } else if (isMateForOpponent) {
      result = 1;
    }

    return result;
  }

  /**
   * Метод для рандомного хода в симуляции.
   *
   * @param simulationBoard Доска.
   * @param movingColor Цвет ходящего.
   * @return Ход.
   */
  public Move simulationRandomMove(Board simulationBoard, Color movingColor) {
    List<Move> possibleMoves = new ArrayList<>();
    List<Position> positions = simulationBoard.getAllPiecePositionByColor(movingColor);
    for (Position position : positions) {
      possibleMoves.addAll(
          simulationBoard.getPiece(position).getAllMoves(simulationBoard, position));
    }
    if (possibleMoves.isEmpty()) {
      return null;
    }
    int ind = (int) (Math.random() * possibleMoves.size());
    Move move = possibleMoves.get(ind);
    return move;
  }

  /**
   * Основной алгоритм бота.
   *
   * @param state Состояние.
   * @param depth Глубина поиска.
   * @param maximizingPlayer Игрок.
   * @return Цену лучшего состояния.
   */
  public int minimaxMcts(State state, int depth, boolean maximizingPlayer, int alpha, int beta) {
    strategy.setTerminalCost(state);
    if (state.isTerminal() || depth == 0) {
      if (state.isTerminal()) {
        state.setValue(strategy.evaluate(state));
      } else if (maximizingPlayer) {
        state.setValue(runSimulations(state.getBoard(), state.getMovingColor(), 5));
      } else {
        state.setValue(strategy.evaluate(state));
      }
      return state.getValue();
    }

    int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    List<State> children = createChildStates(state);
    state.setChildren(children);
    for (State child : children) {
      int value = minimaxMcts(child, depth - 1, !maximizingPlayer, alpha, beta);

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
   * Обновляет значение в вершине и ход.
   *
   * @param node Вершина.
   * @param child Ребенок.
   * @param bestValue Лучшее значение.
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
    minimaxMcts(mainState, searchDepth, true,Integer.MIN_VALUE, Integer.MAX_VALUE);
    return mainState.getMove();
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }
}
