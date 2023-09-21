package io.deeplay.grandmastery.botfarm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class BotFarmTest {
  @BeforeAll
  public static void offLoggers() {
    Logger botFarmLogger = (Logger) LoggerFactory.getLogger(BotFarm.class);
    botFarmLogger.setLevel(Level.OFF);
  }

  @Test
  void runTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          var serverThread = new Thread(() -> BotFarm.run(2023));
          serverThread.start();
          serverThread.interrupt();
        });
  }

  @Test
  public void getPortFromConfigTest() throws IOException {
    Assertions.assertEquals(2023, BotFarm.getPortFromConfig());
  }
}
