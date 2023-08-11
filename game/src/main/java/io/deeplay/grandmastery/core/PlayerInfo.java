package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;

public interface PlayerInfo {
  String getLastMove();

  String getName();

  Color getColor();
}
