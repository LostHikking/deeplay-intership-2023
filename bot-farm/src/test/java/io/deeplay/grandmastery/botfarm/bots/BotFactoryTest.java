package io.deeplay.grandmastery.botfarm.bots;

import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
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
}
