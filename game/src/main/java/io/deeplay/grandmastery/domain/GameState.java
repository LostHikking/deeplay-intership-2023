package io.deeplay.grandmastery.domain;

public enum GameState {
  INIT_STATE,
  WHITE_MOVE,
  BLACK_MOVE,
  ROLLBACK,
  IMPOSSIBLE_MOVE,
  WHITE_WIN,
  BLACK_WIN,
  STALEMATE
}
