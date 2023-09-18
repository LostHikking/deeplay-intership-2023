package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum Bots {
  RANDOMUS("Randomus", Randomus::new),
  MINIMAXIMUS("Minimaximus", color -> new Minimaximus(color, 3)),
  NEGAMAXIMUS("Negamaximus", color -> new Negamaximus(color, 3)),
  MINIMAX("MotoMaxBot", color -> new MiniMaxBot("Moto", color, 3)),
  LJEDMITRY("LjeDmitry", LjeDmitryBot::new);

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
