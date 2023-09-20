package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;

/** Интерфейс для алгоритмов поиска лучшего хода в шахматной игре. */
public interface Algorithm {

  /**
   * Выполняет поиск лучшего хода для заданной доски и истории игры.
   *
   * @param board Доска.
   * @param gameHistory История игры.
   * @return Лучший найденный ход.
   */
  Move findBestMove(Board board, GameHistory gameHistory);

  /**
   * Возвращает лучший найденный ход после истечения времени ожидания.
   *
   * @return Лучший найденный ход.
   */
  Move getBestMoveAfterTimout();
}
