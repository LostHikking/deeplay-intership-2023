package io.deeplay.grandmastery.botfarm;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BotFarmTest {
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
