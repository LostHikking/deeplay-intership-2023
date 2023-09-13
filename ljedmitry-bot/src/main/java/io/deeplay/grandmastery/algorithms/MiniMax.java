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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Реализация алгоритма MiniMax для поиска лучшего хода в игре. */
public class MiniMax implements Algorithm {
  private final Color botColor;
  private final int deep;
  private final boolean isMax;
  private final Map<Move, Double> moveThree;
  private final Bonuses ourBonuses;
  private final Bonuses opponentBonuses;

  /**
   * Создает новый экземпляр алгоритма MiniMax.
   *
   * @param playerInfo Информация об игроке, для которого применяется алгоритм.
   * @param deep Глубина поиска в дереве MiniMax.
   */
  public MiniMax(PlayerInfo playerInfo, int deep) {
    this.botColor = playerInfo.getColor();
    this.isMax = true;
    this.deep = deep;
    this.ourBonuses = new Bonuses();
    this.opponentBonuses = new Bonuses();
    this.moveThree = new HashMap<>();
  }

  @Override
  public Move findBestMove(Board board, GameHistory gameHistory) {
    moveThree.clear();
    return minmax(board, gameHistory, botColor, this.deep, MIN_EVAL, MAX_EVAL, this.isMax);
  }

  private Move minmax(
      Board board,
      GameHistory gameHistory,
      Color color,
      int deep,
      double alpha,
      double beta,
      boolean isMax) {
    if (deep == 0 || isGameOver(board, gameHistory)) {
      moveThree.put(
          board.getLastMove(),
          Evaluation.evaluationFunc(board, gameHistory, botColor, ourBonuses, opponentBonuses));
      return board.getLastMove();
    }

    List<Move> allMoves = getPossibleMoves(board, color);
    Move bestMove = allMoves.get(0);
    moveThree.put(bestMove, isMax ? MIN_EVAL : MAX_EVAL);

    for (Move move : allMoves) {
      Board copyBoard = copyAndMove(move, board);
      GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);
      double eval =
          moveThree.get(
              minmax(copyBoard, copyHistory, inversColor(color), deep - 1, alpha, beta, !isMax));

      if (isMax) {
        if (eval > alpha) {
          alpha = eval;
          moveThree.put(move, eval);
          bestMove = move;
        }
      } else {
        if (eval < beta) {
          beta = eval;
          moveThree.put(move, eval);
          bestMove = move;
        }
      }

      if (beta <= alpha) {
        break;
      }
    }

    return bestMove;
  }
}
