package io.deeplay.grandmastery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerUtils {
  /**
   * Метод отправляет данные в bufferedWriter.
   *
   * @param bufferedWriter bufferedWriter
   * @param text Text
   * @throws IOException Ошибка ввода/вывода
   */
  public static void send(BufferedWriter bufferedWriter, String text) throws IOException {
    bufferedWriter.write(text);
    bufferedWriter.newLine();
    bufferedWriter.flush();

    log.info("Отправили данные на клиент - " + text);
  }

  /**
   * Функция читает Json из BufferedReader.
   *
   * @param in BufferedReader.
   * @return Json
   * @throws IOException Ошибка ввода-вывода
   */
  public static String getJsonFromClient(BufferedReader in) throws IOException {
    var sb = new StringBuilder(in.readLine());

    while (in.ready()) {
      sb.append(in.readLine());
    }

    var result = sb.toString();
    log.info("Получили данные от клиента - " + result);

    return result;
  }
}
