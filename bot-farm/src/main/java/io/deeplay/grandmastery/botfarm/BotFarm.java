package io.deeplay.grandmastery.botfarm;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.botfarm.build.ITask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.util.ServiceLoader;
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

  public BotFarm() {
    build();
  }

  /**
   * Метод запускает сервер на указанном порту.
   *
   * @throws IllegalStateException Ошибка на сервере
   */
  public void run() {
    try (var serverSocket = new ServerSocket(2023)) {
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

  /** Функция запускает задачи. */
  public static void build() {
    for (var implClass : ServiceLoader.load(ITask.class)) {
      implClass.init();
    }
  }

  public static void main(String[] args) {
    new BotFarm().run();
  }
}
