package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;

public interface PlayerListener {
  void makeMove() throws GameException;

  Move getMoveData();

  String getName();

  Color getColor();
}
