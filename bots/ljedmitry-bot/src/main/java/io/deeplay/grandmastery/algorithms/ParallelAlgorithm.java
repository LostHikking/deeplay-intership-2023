package io.deeplay.grandmastery.algorithms;

/** Интерфейс для параллельных алгоритмов поиска лучшего хода в шахматной игре. */
public interface ParallelAlgorithm extends Algorithm {

  /** Завершает работу пула потоков, используемого параллельным алгоритмом. */
  void shutdownPool();
}
