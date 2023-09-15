package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BotFactoryTest {

  @Test
  void createRandomBotTest() {
    Player bot = BotFactory.create("Randomus", Color.WHITE);
    Assertions.assertAll(
        () -> Assertions.assertEquals("Randomus", bot.getName()),
        () -> Assertions.assertEquals(Color.WHITE, bot.getColor()));
  }

  @Test
  void createLjeDmitryBotTest() {
    Player bot = BotFactory.create("LjeDmitry", Color.WHITE);
    Assertions.assertAll(
        () -> Assertions.assertEquals("LjeDmitry", bot.getName()),
        () -> Assertions.assertEquals(Color.WHITE, bot.getColor()));
  }

  @Test
  void createMelniknowBotTest() {
    Player bot = BotFactory.create("Minimaximus", Color.WHITE);
    Assertions.assertAll(
        () -> Assertions.assertEquals("Melniknow-minimaximus", bot.getName()),
        () -> Assertions.assertEquals(Color.WHITE, bot.getColor()));
  }

  @Test
  void getBotsListTest() {
    Set<String> expectBots = Set.of("Randomus", "LjeDmitry", "Minimaximus", "MotoMaxBot");
    Assertions.assertEquals(expectBots, new HashSet<>(Bots.getBotsList()));
  }
}
