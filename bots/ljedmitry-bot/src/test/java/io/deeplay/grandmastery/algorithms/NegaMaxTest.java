package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.checks.ChessPuzzles;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Game;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class NegaMaxTest {
  private static final int DEEP = 3;

  /**
   * Проводит тестирование алгоритма NegaMax на шахматных задачах.
   *
   * @param color Цвет, за который играет бот.
   * @param board Исходное состояние доски, с задачей.
   * @param expect Ожидаемый список ходов, решающих задачу.
   * @param enemyMoves Очередь ходов для противника.
   * @param puzzleName Название шахматной задачи для отображения в отчете.
   * @throws GameException Если возникают ошибки при игре или обработке ходов.
   */
  @ParameterizedTest
  @MethodSource("chessPuzzles")
  public void negamaxChessPuzzlesTest(
      Color color, Board board, List<Move> expect, Queue<Move> enemyMoves, String puzzleName)
      throws GameException {
    NegaMax negaMax = new NegaMax(color, DEEP, 2);
    List<Move> actualMoves = new ArrayList<>();
    GameHistory gameHistory = new GameHistory();
    Game testGame = new Game();
    testGame.startup(board);
    gameHistory.startup(board);

    if (color == Color.BLACK) {
      testGame.setGameState(GameState.BLACK_MOVE);
    }

    for (int i = 0; i < expect.size(); i++) {
      Move bestMove = negaMax.findBestMove(testGame.getCopyBoard(), gameHistory);
      actualMoves.add(bestMove);
      makeMove(testGame, gameHistory, bestMove);

      if (!enemyMoves.isEmpty()) {
        makeMove(testGame, gameHistory, enemyMoves.poll());
      }
    }

    Assertions.assertEquals(expect, actualMoves, puzzleName);
    negaMax.shutdownPool();
  }

  /**
   * Выполняет ход в игре и добавляет его в историю.
   *
   * @param game Игра, в которой выполняется ход.
   * @param gameHistory История игры.
   * @param move Ход.
   * @throws GameException Если возникают ошибки при игре или обработке ходов.
   */
  private void makeMove(Game game, GameHistory gameHistory, Move move) throws GameException {
    game.makeMove(move);
    gameHistory.addBoard(game.getCopyBoard());
    gameHistory.makeMove(move);
  }

  /**
   * Создает аргументы для шахматных задач, используемых в тестировании.
   *
   * @return Поток аргументов для тестирования.
   */
  private static Stream<Arguments> chessPuzzles() {
    return Stream.of(
        ChessPuzzles.easyWhitePuzzle(),
        ChessPuzzles.easyBlackPuzzle(),
        ChessPuzzles.blackCheckmateInOneMove(),
        ChessPuzzles.normalBlackPuzzle());
  }
}
