package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

public class ServerTest {

  /** Отключает логи. */
  @BeforeAll
  public static void offLoggers() {
    Logger serverDaoLogger = (Logger) LoggerFactory.getLogger(ServerDao.class);
    Logger serverGameLogger = (Logger) LoggerFactory.getLogger(ServerGame.class);

    serverDaoLogger.setLevel(Level.OFF);
    serverGameLogger.setLevel(Level.OFF);
  }

  @Test
  void runGameTest() {
    var serverGame =
        new ServerGame(
            new Randomus(Color.WHITE), new Randomus(Color.BLACK), ChessType.CLASSIC, null);

    Assertions.assertDoesNotThrow(serverGame::run);
  }

  @Test
  void runGameFailTest() throws IOException {
    var out = new BufferedWriter(new OutputStreamWriter(System.out, UTF_8));
    out.close();

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), out, "name", Color.BLACK, ChessType.CLASSIC);
    var serverGame =
        new ServerGame(new Randomus(Color.WHITE), serverPlayer, ChessType.CLASSIC, null);

    Assertions.assertDoesNotThrow(serverGame::run);
  }

  @Test
  public void getPortFromConfigTest() throws IOException {
    Assertions.assertEquals(8080, Server.getPortFromConfig());
  }
}
