package io.deeplay.grandmastery.botfarm.bots;

import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.minimaximus.Minimaximus;
import java.util.function.Function;

public enum Bots {
  RANDOMUS("Randomus", (Randomus::new)),
  MINIMAXIMUS("Minimaximus", (color -> new Minimaximus(color, 3)));

  public final String name;
  public final Function<Color, Player> constructor;

  Bots(String name, Function<Color, Player> constructor) {
    this.name = name;
    this.constructor = constructor;
  }
}
