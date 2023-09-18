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
import java.util.List;

public class NegaMax implements Algorithm {
  private final Color botColor;
  private final int deep;
  private final Bonuses ourBonuses;
  private final Bonuses enemyBonuses;

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
    return negamax(board, gameHistory, botColor, deep, MIN_EVAL, MAX_EVAL).move;
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

    List<Move> allMoves = getPossibleMoves(board, color);
    Node bestMove = new Node(allMoves.get(0), MIN_EVAL);
    if (deep == this.deep) {
      this.bestMove = bestMove;
    }

    double maxEval = MIN_EVAL;
    for (Move move : allMoves) {
      Board copyBoard = copyAndMove(move, board);
      GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);
      double eval =
          -negamax(copyBoard, copyHistory, inversColor(color), deep - 1, -beta, -alpha).eval;

      if (eval > maxEval) {
        maxEval = eval;
        bestMove.move = move;
        bestMove.eval = maxEval;
      }
      alpha = Math.max(alpha, eval);

      if (alpha >= beta) {
        break;
      }
    }

    return bestMove;
  }

  @Override
  public Move getBestMoveAfterTimout() {
    return bestMove.move;
  }
}
