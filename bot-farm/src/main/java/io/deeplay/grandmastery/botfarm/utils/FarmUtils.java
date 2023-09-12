package io.deeplay.grandmastery.botfarm.utils;

import io.deeplay.grandmastery.exceptions.QueryException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/** Класс-утилита для ботов. */
@Slf4j
public class FarmUtils {
  /**
   * Метод отправляет данные в BufferedWriter.
   *
   * @param out BufferedWriter
   * @param json Данные
   * @throws IOException Ошибка при отправке данных
   */
  public static void send(BufferedWriter out, String json) throws IOException {
    log.info("Отправили данные клиенту - " + json);
    out.write(json);
    out.newLine();
    out.flush();
  }

  /**
   * Метод получает json из сервера.
   *
   * @param in BufferedReader
   * @return Json
   * @throws IOException Ошибка чтения/записи
   * @throws QueryException Server disconnect
   */
  public static String getJsonFromServer(BufferedReader in) throws IOException {
    var result = in.readLine();

    if (result == null) {
      throw new QueryException("Server disconnect");
    }

    log.info("Получили данные от сервера - " + result);
    return result;
  }
}
