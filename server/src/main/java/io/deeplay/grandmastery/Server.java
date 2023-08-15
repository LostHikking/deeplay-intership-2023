package io.deeplay.grandmastery;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {
  public static final int PORT = 8080;
  public static final ExecutorService START_GAME = Executors.newFixedThreadPool(8);
  public static final ExecutorService GAMES = Executors.newFixedThreadPool(8);

  /**
   * Метод запускает сервер на указанном порту.
   *
   * @throws RuntimeException В случае ошибки сервера
   * @throws IOException Ошибка ввода/вывода
   */
  public static void run() throws IOException {
    try (var serverSocket = new ServerSocket(PORT)) {
      log.info("Сервер запущен!");

      while (!serverSocket.isClosed()) {
        var socket = serverSocket.accept();
        socket.setSoTimeout((int) TimeUnit.MINUTES.toMillis(5));

        START_GAME.execute(new CreateGame(socket));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      GAMES.shutdownNow();
      START_GAME.shutdownNow();

      for (ServerPlayer player : CreateGame.players) {
        player.getSocket().close();
      }

      log.info("Сервер остановлен!");
    }
  }

  /**
   * Метод запускает сервер.
   *
   * @throws IOException Ошибка ввода/вывода
   */
  public static void main(String[] args) throws IOException {
    Server.run();
  }
}
