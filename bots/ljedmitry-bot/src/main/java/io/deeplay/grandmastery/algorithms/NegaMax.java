package io.deeplay.grandmastery.algorithms;

import static io.deeplay.grandmastery.algorithms.Evaluation.MAX_EVAL;
import static io.deeplay.grandmastery.algorithms.Evaluation.MIN_EVAL;
import static io.deeplay.grandmastery.utils.Algorithms.copyAndMove;
import static io.deeplay.grandmastery.utils.Algorithms.copyHistoryAndMove;
import static io.deeplay.grandmastery.utils.Algorithms.getPossibleMoves;
import static io.deeplay.grandmastery.utils.Algorithms.isGameOver;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Реализация алгоритма NegaMax для поиска лучшего хода в шахматах.
 *
 * <p>Алгоритм NegaMax - это улучшенная версия алгоритма Minimax для поиска оптимальных ходов с
 * использованием альфа-бета отсечения.
 *
 * <p>Этот класс позволяет выполнять поиск лучшего хода в параллельном режиме с использованием
 * ForkJoinPool.
 */
public class NegaMax implements ParallelAlgorithm {
  private final Color botColor;
  private final int deep;
  private final ForkJoinPool pool;

  private Node bestMove;

  /**
   * Создает новый экземпляр класса NegaMax.
   *
   * @param color Цвет бота.
   * @param deep Глубина поиска.
   */
  public NegaMax(Color color, int deep) {
    this.botColor = color;
    this.deep = deep;
    this.bestMove = null;
    this.pool = new ForkJoinPool(6);
  }

  /**
   * Создает новый экземпляр класса NegaMax с указанным количеством потоков.
   *
   * @param color Цвет бота.
   * @param deep Глубина поиска.
   * @param parallelism Количество параллельных потоков.
   */
  public NegaMax(Color color, int deep, int parallelism) {
    this.botColor = color;
    this.deep = deep;
    this.bestMove = null;
    this.pool = new ForkJoinPool(parallelism);
  }

  @Override
  public Move findBestMove(Board board, GameHistory gameHistory) {
    bestMove = null;
    NegamaxTask task =
        new NegamaxTask(board, gameHistory, botColor, deep, MIN_EVAL, MAX_EVAL, null);

    return pool.invoke(task).move;
  }

  /** Задача для параллельного выполнения алгоритма NegaMax. */
  private class NegamaxTask extends RecursiveTask<Node> {
    private final Board board;
    private final GameHistory gameHistory;
    private final Color color;
    private final int deep;
    private final double beta;
    private final double alpha;
    private final Move move;

    /**
     * Создает новую задачу NegaMaxTask.
     *
     * @param board Доска.
     * @param gameHistory История игры.
     * @param color Цвет бота.
     * @param deep Глубина поиска.
     * @param alpha Значение альфа (верхняя граница) для альфа-бета отсечения.
     * @param beta Значение бета (нижняя граница) для альфа-бета отсечения.
     * @param move Ход, связанный с этой задачей.
     */
    public NegamaxTask(
        Board board,
        GameHistory gameHistory,
        Color color,
        int deep,
        double alpha,
        double beta,
        Move move) {
      this.board = board;
      this.gameHistory = gameHistory;
      this.color = color;
      this.deep = deep;
      this.alpha = alpha;
      this.beta = beta;
      this.move = move;
    }

    /**
     * Выполняет алгоритм NegaMax для поиска лучшего хода.
     *
     * @param board Доска.
     * @param gameHistory История игры.
     * @param color Цвет бота.
     * @param deep Глубина поиска.
     * @param alpha Значение альфа (верхняя граница) для альфа-бета отсечения.
     * @param beta Значение бета (нижняя граница) для альфа-бета отсечения.
     * @return Лучший найденный ход и его оценка.
     */
    private Node negamax(
        Board board, GameHistory gameHistory, Color color, int deep, double alpha, double beta) {
      if (deep == 0 || isGameOver(board, gameHistory)) {
        double signEval = color == botColor ? 1.0 : -1.0;
        boolean isBotMove = color == botColor;

        return new Node(
            board.getLastMove(),
            Evaluation.evaluationFunc(board, gameHistory, botColor, isBotMove) * signEval);
      }

      List<Move> moves = getPossibleMoves(board, color);
      Node bestMove = new Node(moves.get(0), MIN_EVAL);
      if (deep == NegaMax.this.deep) {
        NegaMax.this.bestMove = bestMove;
      }

      List<NegamaxTask> tasks = new ArrayList<>();
      for (int i = 0; i < moves.size(); i++) {
        Board copyBoard = copyAndMove(moves.get(i), board);
        GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);

        if (i == 0 || getPool().getQueuedTaskCount() > 0) {
          double eval =
              -negamax(copyBoard, copyHistory, color.getOpposite(), deep - 1, -beta, -alpha).eval;

          if (eval > alpha) {
            alpha = eval;
            bestMove.move = moves.get(i);
            bestMove.eval = eval;
          }

          if (alpha >= beta) {
            break;
          }
        } else {
          NegamaxTask task =
              new NegamaxTask(
                  copyBoard,
                  copyHistory,
                  color.getOpposite(),
                  deep - 1,
                  -beta,
                  -alpha,
                  moves.get(i));
          task.fork();
          tasks.add(task);
        }
      }

      for (NegamaxTask task : tasks) {
        if (task.join() == null) {
          break;
        } else {
          Node node = task.getRawResult();
          if (-node.eval > alpha) {
            alpha = -node.eval;
            bestMove.move = task.move;
            bestMove.eval = alpha;
          }
        }

        if (alpha >= beta) {
          break;
        }
      }

      return bestMove;
    }

    @Override
    protected Node compute() {
      if (alpha >= beta) {
        return null;
      }
      return negamax(board, gameHistory, color, deep, alpha, beta);
    }
  }

  @Override
  public void shutdownPool() {
    pool.shutdown();
  }

  @Override
  public Move getBestMoveAfterTimout() {
    return bestMove.move;
  }
}
