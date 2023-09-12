package io.deeplay.grandmastery.botfarm;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BotFarm {
  public static final ExecutorService CREATE_PLAYER = Executors.newSingleThreadExecutor();
  public static final ExecutorService PLAYERS = Executors.newFixedThreadPool(8);

  /**
   * Метод запускает сервер на указанном порту.
   *
   * @throws IllegalStateException Ошибка на сервере
   */
  public static void run(int port) {
    try (var serverSocket = new ServerSocket(port)) {
      log.info("Server run");
      while (!serverSocket.isClosed()) {
        var socket = serverSocket.accept();
        socket.setSoTimeout((int) TimeUnit.MINUTES.toMillis(5));

        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

        CREATE_PLAYER.execute(new CreatePlayer(socket, in, out));
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      CREATE_PLAYER.shutdownNow();
    }
  }

  protected static int getPortFromConfig() throws IOException {
    try (InputStream config =
        Main.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties properties = new Properties();
      properties.load(config);

      return Integer.parseInt(properties.getProperty("port"));
    }
  }

  /** Метод запускает бот ферму. */
  public static void main(String[] args) {
    try {
      BotFarm.run(getPortFromConfig());
    } catch (Exception e) {
      log.error("Ошибка во время работы бот фермы", e);
    }
  }
}
