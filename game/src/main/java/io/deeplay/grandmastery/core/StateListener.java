package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.GameState;

public interface StateListener {
  void changeGameState(GameState gameState);
}
