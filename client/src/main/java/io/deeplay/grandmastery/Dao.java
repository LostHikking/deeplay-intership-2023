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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dao {
  private static final Logger logger = LoggerFactory.getLogger(Dao.class);

  private final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;

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

    logger.info("Создали DAO класс");
  }

  /**
   * Функция отправляет запрос на сервер и возвращает ответ от него.
   *
   * @param dto Request Dto
   * @param clazz Response Dto class
   * @return Response Dto
   * @throws QueryException Ошибка выполнения запроса
   */
  public <T extends IDto> T query(IDto dto, Class<T> clazz) throws QueryException {
    try {
      logger.info("Отправка запроса на сервер - " + dto);
      var json = ConversationService.serialize(dto);

      out.write(json);
      out.newLine();
      out.flush();

      var response = in.readLine();
      return ConversationService.deserialize(response, clazz);
    } catch (IOException e) {
      logger.error("Во время выполнения запроса возникла ошибка - " + e.getMessage());
      throw new QueryException(e.getMessage());
    }
  }

  /**
   * Функция socket.
   *
   * @throws IOException Ошибка чтения/записи
   */
  public void close() throws IOException {
    socket.close();
    logger.info("Закрыли соединение");
  }
}
