package io.deeplay.grandmastery;

import io.deeplay.grandmastery.algorithms.NegaMax;
import io.deeplay.grandmastery.domain.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LjeDmitryBotTest {
  @Test
  void createDefaultBotTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE);
    Assertions.assertTrue(bot.getAlgorithm() instanceof NegaMax);
  }

  @Test
  void answerDrawTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE);
    Assertions.assertFalse(bot.answerDraw());
  }
}
