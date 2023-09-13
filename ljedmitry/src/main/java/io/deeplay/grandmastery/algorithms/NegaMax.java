package io.deeplay.grandmastery.algorithms;

import static io.deeplay.grandmastery.utils.Algorithms.MAX_EVAL;
import static io.deeplay.grandmastery.utils.Algorithms.MIN_EVAL;
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
import io.deeplay.grandmastery.domain.TranspositionFlags;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NegaMax implements Algorithm {
  private final Color botColor;
  private final int deep;
  private final Map<Board, TEntry> transpositionTable;
  private final Map<Move, Double> moveThree;
  private final Bonuses bonuses;

  private static class TEntry {
    protected int depth;
    protected double value;
    protected TranspositionFlags flag;

    TEntry(int depth, double value, TranspositionFlags flag) {
      this.depth = depth;
      this.value = value;
      this.flag = flag;
    }
  }

  public NegaMax(PlayerInfo playerInfo, int deep) {
    this.botColor = playerInfo.getColor();
    this.deep = deep;
    this.transpositionTable = new HashMap<>();
    this.moveThree = new HashMap<>();
    this.bonuses = new Bonuses();
  }

  @Override
  public Move findBestMove(Board board, GameHistory gameHistory) {
    moveThree.clear();
    return negamax(board, gameHistory, botColor, this.deep, MIN_EVAL, MAX_EVAL);
  }

  private Move negamax(
      Board board, GameHistory gameHistory, Color color, int deep, double alpha, double beta) {
    if (transpositionTable.containsKey(board)) {
      TEntry tEntry = transpositionTable.get(board);
      if (tEntry.depth >= deep) {
        if (tEntry.flag == TranspositionFlags.EXACT) {
          moveThree.put(board.getLastMove(), tEntry.value);
          return board.getLastMove();
        } else if (tEntry.flag == TranspositionFlags.LOWER_BOUND) {
          alpha = Math.max(alpha, tEntry.value);
        } else if (tEntry.flag == TranspositionFlags.UPPER_BOUND) {
          beta = Math.min(beta, tEntry.value);
        }

        if (alpha >= beta) {
          moveThree.put(board.getLastMove(), tEntry.value);
          return board.getLastMove();
        }
      }
    }

    if (deep == 0 || isGameOver(board, gameHistory)) {
      int signEval = color == botColor ? 1 : -1;
      moveThree.put(
          board.getLastMove(),
			  Evaluation.evaluationFunc(board, gameHistory, botColor, bonuses, new Bonuses()) * signEval);
      return board.getLastMove();
    }

    List<Move> allMoves = getPossibleMoves(board, color);
    Move bestMove = allMoves.get(0);
    moveThree.put(bestMove, MIN_EVAL);
    double b = beta;

    for (Move move : allMoves) {
      Board copyBoard = copyAndMove(move, board);
      GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);
      double eval =
          -moveThree.get(negamax(copyBoard, copyHistory, inversColor(color), deep - 1, -b, -alpha));

      if (eval > alpha) {
        if (alpha == b || deep == 1) {
          alpha = eval;
        } else {
          alpha =
              -moveThree.get(
                  negamax(copyBoard, copyHistory, inversColor(color), deep - 1, -beta, -alpha));
        }
        moveThree.put(move, eval);
        bestMove = move;
      }

      if (alpha >= beta) {
        break;
      }

      b = alpha + 1;
    }

    double bestEval = moveThree.get(bestMove);
    TranspositionFlags flag = TranspositionFlags.EXACT;
    if (bestEval <= alpha) {
      flag = TranspositionFlags.UPPER_BOUND;
    } else if (bestEval >= beta) {
      flag = TranspositionFlags.LOWER_BOUND;
    }

    transpositionTable.put(board, new TEntry(deep, bestEval, flag));
    return bestMove;
  }
}
