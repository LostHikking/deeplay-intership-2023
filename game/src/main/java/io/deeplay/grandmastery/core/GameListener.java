package io.deeplay.grandmastery.core;

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
   * @param board текущее состояние доски
   */
  void makeMove(Move move, Board board);

  /**
   * Вызывается при откате (отмене) хода для возврата к предыдущему состоянию доски.
   *
   * @param board состояние доски после отката
   */
  void rollback(Board board);
}
