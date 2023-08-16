package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.exceptions.QueryException;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Dao {
  private final Socket socket;
  @Getter private final BufferedReader in;
  @Getter private final BufferedWriter out;

  /**
   * Конструктор DAO.
   *
   * @param socket Socket
   * @throws IOException Ошибка чтения/записи
   */
  public Dao(Socket socket) throws IOException {
    this.socket = socket;
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
    this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

    log.info("Создали DAO класс");
  }

  /**
   * Функция отправляет запрос на сервер и возвращает ответ от него.
   *
   * @param dto Request Dto
   * @return Response Dto
   * @throws QueryException Ошибка выполнения запроса
   */
  public IDto query(IDto dto) throws QueryException {
    try {
      log.info("Отправка запроса на сервер - " + dto);
      var json = ConversationService.serialize(dto);

      out.write(json);
      out.newLine();
      out.flush();

      var response = in.readLine();
      log.info("Получили ответ от сервера - " + response);

      return ConversationService.deserialize(response);
    } catch (IOException e) {
      log.error("Во время выполнения запроса возникла ошибка - " + e.getMessage());
      throw new QueryException(e.getMessage());
    }
  }

  /**
   * Метод отправляет строку в BufferedWriter.
   *
   * @param text Json text
   * @throws IOException Ошибка ввода/вывода
   */
  public void send(String text) throws IOException {
    out.write(text);
    out.newLine();
    out.flush();

    log.info("Отправили данные на сервер - " + text);
  }

  /**
   * Функция читает Json из BufferedReader.
   *
   * @return Json
   * @throws IOException Ошибка ввода-вывода
   */
  public String getJsonFromServer() throws IOException {
    var result = in.readLine();
    log.info("Получили данные от сервера - " + result);

    return result;
  }

  /**
   * Функция socket.
   *
   * @throws IOException Ошибка чтения/записи
   */
  public void close() throws IOException {
    socket.close();
    log.info("Закрыли соединение");
  }
}
