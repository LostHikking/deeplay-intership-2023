package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.exceptions.GameException;

public interface PlayerListener extends PlayerInfo {
  void makeMove(Board board) throws GameException;
}
