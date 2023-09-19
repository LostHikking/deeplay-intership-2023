package io.deeplay.grandmastery.algorithms;

import static io.deeplay.grandmastery.algorithms.Evaluation.MAX_EVAL;
import static io.deeplay.grandmastery.algorithms.Evaluation.MIN_EVAL;
import static io.deeplay.grandmastery.utils.Algorithms.copyAndMove;
import static io.deeplay.grandmastery.utils.Algorithms.copyHistoryAndMove;
import static io.deeplay.grandmastery.utils.Algorithms.getPossibleMoves;
import static io.deeplay.grandmastery.utils.Algorithms.inversColor;
import static io.deeplay.grandmastery.utils.Algorithms.isGameOver;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.PlayerInfo;
import io.deeplay.grandmastery.domain.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class NegaMax implements ParallelAlgorithm {
  private final Color botColor;
  private final int deep;
  private final Bonuses ourBonuses;
  private final Bonuses enemyBonuses;
  private final ForkJoinPool pool = ForkJoinPool.commonPool();

  private Node bestMove;

  public NegaMax(PlayerInfo playerInfo, int deep) {
    this.botColor = playerInfo.getColor();
    this.deep = deep;
    this.ourBonuses = new Bonuses();
    this.enemyBonuses = new Bonuses();
    this.bestMove = null;
  }

  @Override
  public Move findBestMove(Board board, GameHistory gameHistory) {
    bestMove = null;
    NegamaxTask task =
        new NegamaxTask(board, gameHistory, botColor, deep, MIN_EVAL, MAX_EVAL, null);

    return pool.invoke(task).move;
  }

  private class NegamaxTask extends RecursiveTask<Node> {
    private final Board board;
    private final GameHistory gameHistory;
    private final Color color;
    private final int deep;
    private final double beta;
    private final double alpha;
    private final Move move;

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

    private Node negamax(
        Board board, GameHistory gameHistory, Color color, int deep, double alpha, double beta) {
      if (deep == 0 || isGameOver(board, gameHistory)) {
        double signEval;
        boolean isBotColor;
        if (color == botColor) {
          signEval = 1.0;
          isBotColor = true;
        } else {
          signEval = -1.0;
          isBotColor = false;
        }

        return new Node(
            board.getLastMove(),
            Evaluation.evaluationFunc(
                    board, gameHistory, botColor, ourBonuses, enemyBonuses, isBotColor)
                * signEval);
      }

      List<Move> moves = getPossibleMoves(board, color);
      Node bestMove = new Node(moves.get(0), MIN_EVAL);
      double maxEval = MIN_EVAL;
      if (deep == NegaMax.this.deep) {
        NegaMax.this.bestMove = bestMove;
      }

      List<NegamaxTask> tasks = new ArrayList<>();
      for (int i = 0; i < moves.size(); i++) {
        Board copyBoard = copyAndMove(moves.get(i), board);
        GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);

        if (i == 0 || getPool().getQueuedTaskCount() > 0) {
          double eval =
              -negamax(copyBoard, copyHistory, inversColor(color), deep - 1, -beta, -alpha).eval;

          if (eval > maxEval) {
            maxEval = eval;
            bestMove.move = moves.get(i);
            bestMove.eval = maxEval;
          }
          alpha = Math.max(alpha, eval);

          if (alpha >= beta) {
            break;
          }
        } else {
          NegamaxTask task =
              new NegamaxTask(
                  copyBoard,
                  copyHistory,
                  inversColor(color),
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
          if (-node.eval > maxEval) {
            maxEval = -node.eval;
            bestMove.move = task.move;
            bestMove.eval = maxEval;
          }
          alpha = Math.max(alpha, -node.eval);
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
