package io.deeplay.grandmastery;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  public static final int PORT = 8080;
  public static final List<ServerThread> serverList =
      Collections.synchronizedList(new ArrayList<>());
  public static final ExecutorService pool = Executors.newSingleThreadExecutor();

  /**
   * Метод запускает сервер.
   *
   * @throws IOException Неудачая попытка чтения/записи
   */
  public static void main(String[] args) throws IOException {
    try (var serverSocket = new ServerSocket(PORT)) {
      while (true) {
        var socket = serverSocket.accept();

        pool.execute(
            () -> {
              try {
                //                var in = new BufferedReader(new
                // InputStreamReader(socket.getInputStream(), UTF_8));
                //                var mode = GameMode.valueOf(in.readLine());
                //
                //                if (mode == GameMode.BOT_VS_BOT) {
                //
                //                } else if (mode == GameMode.HUMAN_VS_BOT) {
                //
                //                } else if (mode == GameMode.HUMAN_VS_HUMAN) {
                //
                //                }

                serverList.add(new ServerThread(socket));
              } catch (Exception e) {
                Thread.currentThread().interrupt();
              }
            });
      }
    }
  }
}
