package io.deeplay.grandmastery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BotFarmTest {
  @Test
  void loadPlayersTest() {
    var players = BotFarm.loadPlayers();

    Assertions.assertEquals(2, players.size());
  }

  @Test
  void buildTest() {
    Assertions.assertDoesNotThrow(BotFarm::build);
  }

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
