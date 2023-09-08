package io.deeplay.grandmastery.botfarm.utils;

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
}
