package io.deeplay.grandmastery.listeners;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;

public interface GameListener {
  /**
   * Вызывается при запуске игры для передачи начального состояния доски.
   *
   * @param board начальное состояние доски
   */
  void startup(Board board);

  /**
   * Вызывается при совершении игрового хода для обработки этого хода и текущего состояния доски.
   *
   * @param move совершенный игровой ход
   */
  void makeMove(Move move);

  /** Вызывается в конце игры. */
  void gameOver();
}
