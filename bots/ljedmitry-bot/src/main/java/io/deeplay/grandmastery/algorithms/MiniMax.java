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

/** Реализация алгоритма MiniMax для поиска лучшего хода в игре. */
public class MiniMax implements Algorithm {
  private final Color botColor;
  private final int deep;
  private final boolean isMax;
  private final Bonuses ourBonuses;
  private final Bonuses opponentBonuses;
  private Node bestMove;

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
    this.bestMove = null;
  }

  @Override
  public Move findBestMove(Board board, GameHistory gameHistory) {
    this.bestMove = null;
    return minmax(board, gameHistory, botColor, this.deep, MIN_EVAL, MAX_EVAL, this.isMax).move;
  }

  private Node minmax(
      Board board,
      GameHistory gameHistory,
      Color color,
      int deep,
      double alpha,
      double beta,
      boolean isMax) {
    if (deep == 0 || isGameOver(board, gameHistory)) {
      double eval =
          Evaluation.evaluationFunc(
              board, gameHistory, botColor, ourBonuses, opponentBonuses, isMax);
      return new Node(board.getLastMove(), eval);
    }

    List<Move> allMoves = getPossibleMoves(board, color);
    Node bestMove = new Node(allMoves.get(0), isMax ? MIN_EVAL : MAX_EVAL);
    if (deep == this.deep) {
      this.bestMove = bestMove;
    }

    for (Move move : allMoves) {
      Board copyBoard = copyAndMove(move, board);
      GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);
      double eval =
          minmax(copyBoard, copyHistory, inversColor(color), deep - 1, alpha, beta, !isMax).eval;

      if (isMax) {
        if (eval > alpha) {
          alpha = eval;
          bestMove.move = move;
          bestMove.eval = eval;
        }
      } else {
        if (eval < beta) {
          beta = eval;
          bestMove = new Node(move, eval);
        }
      }

      if (beta <= alpha) {
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
