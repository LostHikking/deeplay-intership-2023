package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

  /** Метод запускает клиент.
   * @throws IOException Неудачая попытка чтения/записи
   * */
  public static void main(String[] args) throws IOException {
    try (var socket = new Socket("localhost", 8080);
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
        var reader = new BufferedReader(new InputStreamReader(System.in, UTF_8))) {

      while (true) {
        System.out.println("Введите сообщение:");
        var word = reader.readLine();

        out.write(word + "\n");
        out.flush();

        if ("stop".equals(word)) {
          break;
        }

        var serverWord = in.readLine();
        System.out.println(serverWord);
      }
    } catch (IOException e) {
      throw new IOException();
    }
  }
}
