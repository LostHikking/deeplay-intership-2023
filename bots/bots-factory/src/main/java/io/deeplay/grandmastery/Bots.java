package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.motobots.ExpectiMaxBot;
import io.deeplay.grandmastery.motobots.MiniMaxBot;
import io.deeplay.grandmastery.motostrategies.AttackingStrategy;
import io.deeplay.grandmastery.motostrategies.DefendingStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum Bots {
  RANDOMUS("Randomus", Randomus::new),
  MINIMAXIMUS("Minimaximus", color -> new Minimaximus(color, 3)),
  NEGAMAXIMUS("Negamaximus", color -> new Negamaximus(color, 3)),
  ATTACKING_MINIMAX(
      "AttackingMiniMax(moto)", color -> new MiniMaxBot("Moto", color, new AttackingStrategy(), 3)),
  DEFENDING_MINIMAX(
      "DefendingMiniMax(moto)", color -> new MiniMaxBot("Moto", color, new DefendingStrategy(), 3)),
  ATTACKING_EXPECTIMAX(
      "AttackingExpectiMax(moto)",
          color -> new ExpectiMaxBot("Moto", color, new AttackingStrategy(), 3)),
  DEFENDING_EXPECTIMAX(
      "DefendingExpectiMax(moto)",
          color -> new ExpectiMaxBot("Moto", color, new DefendingStrategy(), 3)),
  DEEPLODOCUS("Deeplodocus", Deeplodocus::new),
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
