package io.deeplay.grandmastery.listeners;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;

public interface GameListener {
  /**
   * Вызывается при запуске игры для передачи начального состояния доски.
   *
   * @param board начальное состояние доски
   * @throws GameException если возникла ошибка во время старта игры
   */
  void startup(Board board) throws GameException;

  /**
   * Вызывается при совершении игрового хода для обработки этого хода и текущего состояния доски.
   *
   * @param move совершенный игровой ход
   * @throws GameException если ход не корректен
   */
  void makeMove(Move move) throws GameException;

  /** Вызывается в конце игры. */
  void gameOver(GameState gameState);
}
