package io.deeplay.grandmastery.bots;

import io.deeplay.grandmastery.LjeDmitryBot;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.minimaximus.Minimaximus;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum Bots {
  RANDOMUS("Randomus", Randomus::new),
  MINIMAXIMUS("Minimaximus", color -> new Minimaximus(color, 3)),
  LJEDMITRY("LjeDmitry", color -> new LjeDmitryBot(color, "minimax", 3));

  public final String name;

  @SuppressWarnings("ImmutableEnumChecker")
  public final Function<Color, Player> constructor;

  Bots(String name, Function<Color, Player> constructor) {
    this.name = name;
    this.constructor = constructor;
  }

  public static List<String> getBotsList() {
    return Arrays.stream(Bots.values()).map(n -> n.name).toList();
  }
}
