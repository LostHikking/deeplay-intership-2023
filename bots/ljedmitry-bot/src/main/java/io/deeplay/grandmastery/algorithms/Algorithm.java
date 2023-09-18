package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;

public interface Algorithm {
  Move findBestMove(Board board, GameHistory gameHistory);

  Move getBestMoveAfterTimout();
}
