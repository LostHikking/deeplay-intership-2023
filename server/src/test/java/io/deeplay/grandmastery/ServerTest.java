package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServerTest {
  @Test
  void runGameTest() {
    var serverGame =
        new ServerGame(
            new AiPlayer(Color.WHITE), new AiPlayer(Color.BLACK), ChessType.CLASSIC, null);

    Assertions.assertDoesNotThrow(serverGame::run);
  }

  @Test
  void runGameFailTest() throws IOException {
    var out = new BufferedWriter(new OutputStreamWriter(System.out, UTF_8));
    out.close();

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(),
            Mockito.mock(),
            out,
            "name",
            Color.BLACK,
            GameMode.HUMAN_VS_HUMAN,
            ChessType.CLASSIC);
    var serverGame =
        new ServerGame(new AiPlayer(Color.WHITE), serverPlayer, ChessType.CLASSIC, null);

    Assertions.assertDoesNotThrow(serverGame::run);
  }
}
