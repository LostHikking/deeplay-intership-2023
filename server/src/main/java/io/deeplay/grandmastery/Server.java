package io.deeplay.grandmastery;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {
  public static final ExecutorService START_GAME = Executors.newSingleThreadExecutor();
  public static final ExecutorService GAMES = Executors.newFixedThreadPool(8);

  /**
   * Метод запускает сервер на указанном порту.
   *
   * @throws IllegalStateException В случае ошибки сервера
   * @throws IOException Ошибка ввода/вывода
   */
  public static void run(int port) throws IOException {
    try (var serverSocket = new ServerSocket(port)) {
      log.info("Сервер запущен!");

      while (!serverSocket.isClosed()) {
        var socket = serverSocket.accept();
        socket.setSoTimeout((int) TimeUnit.MINUTES.toMillis(5));

        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

        START_GAME.execute(new CreateGame(socket, in, out));
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      GAMES.shutdownNow();
      START_GAME.shutdownNow();

      for (ServerPlayer player : CreateGame.players) {
        player.getSocket().close();
      }

      log.info("Сервер остановлен!");
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

  /** Метод запускает сервер. */
  public static void main(String[] args) {
    try {
      Server.run(getPortFromConfig());
    } catch (Exception e) {
      log.error("Ошибка во время работы сервера", e);
    }
  }
}
