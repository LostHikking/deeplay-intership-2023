package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.PlayerInfo;
import io.deeplay.grandmastery.domain.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.deeplay.grandmastery.utils.Algorithms.*;

public class NegaMax implements Algorithm {
  private final Color botColor;
  private final int deep;
  private final Map<Move, Double> moveThree;

  public NegaMax(PlayerInfo playerInfo, int deep) {
    this.botColor = playerInfo.getColor();
    this.deep = deep;
    this.moveThree = new HashMap<>();
  }

  @Override
  public Move findBestMove(Board board, GameHistory gameHistory) {
    moveThree.clear();
    return negamax(board, gameHistory, botColor, this.deep, MIN_EVAL, MAX_EVAL);
  }

  private Move negamax(
      Board board, GameHistory gameHistory, Color color, int deep, double alpha, double beta) {
    if (deep == 0 || isGameOver(board, gameHistory)) {
      int signEval = color == botColor ? 1 : -1;
      moveThree.put(board.getLastMove(), evaluationFunc(board, gameHistory, botColor) * signEval);
      return board.getLastMove();
    }

    List<Move> allMoves = getPossibleMoves(board, color);
    Move bestMove = allMoves.get(0);
    moveThree.put(bestMove, MIN_EVAL);

    for (Move move : allMoves) {
      Board copyBoard = copyAndMove(move, board);
      GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);
      double eval =
          -moveThree.get(
              negamax(copyBoard, copyHistory, inversColor(color), deep - 1, -beta, -alpha));

      if (eval > alpha) {
        alpha = eval;
        moveThree.put(move, eval);
        bestMove = move;

        if (alpha >= beta) {
          break;
        }
      }
    }

    return bestMove;
  }
}
