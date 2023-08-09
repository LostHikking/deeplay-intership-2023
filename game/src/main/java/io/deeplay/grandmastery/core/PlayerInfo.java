package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;

public interface PlayerInfo {
  Move getMoveData();

  String getName();

  Color getColor();
}
