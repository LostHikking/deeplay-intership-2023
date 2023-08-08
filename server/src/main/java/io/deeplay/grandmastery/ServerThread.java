package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerThread extends Thread {
  private final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;

  /**
   * Конструктор для ServerThread.
   * @param socket Сокет
   * @throws IOException Неудачая попытка чтения/записи
   * */
  public ServerThread(Socket socket) throws IOException {
    this.socket = socket;

    in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

    start();
  }

  @Override
  public void run() {
    try {
      while (true) {
        var word = in.readLine();
        System.out.println(word);

        if (word.equals("stop")) {
          socket.close();
          break;
        }

        for (ServerThread vr : Server.serverList) {
          vr.send(word);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  private void send(String msg) {
    try {
      out.write(msg + "\n");
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
