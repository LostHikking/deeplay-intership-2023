package io.deeplay.grandmastery.botfarm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BotFarmTest {
  @Test
  void runTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          var serverThread = new Thread(() -> new BotFarm().run());
          serverThread.start();
          serverThread.interrupt();
        });
  }
}
