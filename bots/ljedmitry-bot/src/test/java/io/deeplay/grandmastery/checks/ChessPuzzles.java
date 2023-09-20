package io.deeplay.grandmastery.checks;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.params.provider.Arguments;

/** Предоставляет шахматные задачи для тестирования алгоритмов. */
public class ChessPuzzles {

  /**
   * Возвращает аргументы легкой шахматной задачи для белых.
   *
   * @return Аргументы для тестирования.
   */
  public static Arguments easyWhitePuzzle() {
    String boardStr = "r_p_N_PR___p_BP__p____PB__bp___Qk_bq_P___p____PRnp____PKrp____P_";
    Board board = Boards.fromString(boardStr);

    List<Move> expectMoves = List.of(LongAlgebraicNotation.getMoveFromString("e4h7"));
    return PuzzleDataProvider.createArguments(
        Color.WHITE, board, expectMoves, new ArrayDeque<>(), "easyWhitePuzzle");
  }

  /**
   * Возвращает аргументы для шахматной задачи "Мат черным в один ход" (легко).
   *
   * @return Аргументы для тестирования.
   */
  public static Arguments blackCheckmateInOneMove() {
    String boardStr = "__P____________________Q____________n_n_______________B_k_____PK";
    Board board = Boards.fromString(boardStr);

    List<Move> expectMoves = List.of(LongAlgebraicNotation.getMoveFromString("e5f7"));
    return PuzzleDataProvider.createArguments(
        Color.WHITE, board, expectMoves, new ArrayDeque<>(), "blackCheckmateInOneMove");
  }

  /**
   * Возвращает аргументы для шахматной задачи "Мат черным в два хода" (нормально).
   *
   * @return Аргументы для тестирования.
   */
  public static Arguments blackCheckmateInTwoMove() {
    String boardStr = "_p___P_R_____P_N___pP___r______R_p__nPQ____q__P__kp__P_K_p___bPB";
    Board board = Boards.fromString(boardStr);

    List<Move> expectMoves = LongAlgebraicNotation.getMovesFromString("d1d8,f4f7");
    Queue<Move> enemyMoves = new ArrayDeque<>(LongAlgebraicNotation.getMovesFromString("e7d8"));
    return PuzzleDataProvider.createArguments(
        Color.WHITE, board, expectMoves, enemyMoves, "blackCheckmateInTwoMove");
  }

  /**
   * Возвращает аргументы легкой шахматной задачи для черных.
   *
   * @return Аргументы для тестирования.
   */
  public static Arguments easyBlackPuzzle() {
    String boardStr = "rp____PR_p_____N_p_____Bqb_P___Q_b_nn_P_rp___NPRkp___PBK_p____P_";
    Board board = Boards.fromString(boardStr);

    List<Move> expectMoves = List.of(LongAlgebraicNotation.getMoveFromString("f6e4"));
    return PuzzleDataProvider.createArguments(
        Color.BLACK, board, expectMoves, new ArrayDeque<>(), "easyBlackPuzzle");
  }

  /**
   * Возвращает аргументы шахматной задачи, среднего уровня, для черных.
   *
   * @return Аргументы для тестирования.
   */
  public static Arguments normalBlackPuzzle() {
    String boardStr = "rp____PR_pp___P_____P_____pP__Q_k_pnN__K___q__PB_p__n_P_rp____PR";
    Board board = Boards.fromString(boardStr);

    List<Move> expectMoves = LongAlgebraicNotation.getMovesFromString("e5d3,d3f4");
    Queue<Move> enemyMoves = new ArrayDeque<>(LongAlgebraicNotation.getMovesFromString("e1f1"));
    return PuzzleDataProvider.createArguments(
        Color.BLACK, board, expectMoves, enemyMoves, "normalBlackPuzzle");
  }

  /**
   * Возвращает аргументы шахматной задачи, среднего уровня, для белых.
   *
   * @return Аргументы для тестирования.
   */
  public static Arguments normalWhitePuzzle() {
    String boardStr = "______P_____p_k_______________P__________R________K_n________PP_";
    Board board = Boards.fromString(boardStr);

    List<Move> expectMoves = LongAlgebraicNotation.getMovesFromString("g5e4,e4f2,b7a7");
    Queue<Move> enemyMoves =
        new ArrayDeque<>(LongAlgebraicNotation.getMovesFromString("g3f3,f3f2"));
    return PuzzleDataProvider.createArguments(
        Color.WHITE, board, expectMoves, enemyMoves, "normalWhitePuzzle");
  }
}
