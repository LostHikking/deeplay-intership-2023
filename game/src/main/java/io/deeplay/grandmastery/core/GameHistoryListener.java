package io.deeplay.grandmastery.core;

public interface GameHistoryListener {
  /**
   * Проверяет, является ли текущая ситуация на доске ничьей.
   *
   * @param board текущее состояние доски
   * @return {@code true}, если текущая ситуация является ничьей, иначе {@code false}
   */
  boolean checkIsDraw(Board board);
}
