package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServerTest {
  @Test
  void runGameTest() {
    var serverGame =
        new ServerGame(
            new AiPlayer(Color.WHITE), new AiPlayer(Color.BLACK), ChessType.CLASSIC, null);

    Assertions.assertDoesNotThrow(serverGame::run);
  }
}
