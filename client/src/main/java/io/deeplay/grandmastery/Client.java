package io.deeplay.grandmastery;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.dto.StartGameRequestDto;
import io.deeplay.grandmastery.dto.StartGameResponseDto;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private static final String HOST = "localhost";
  private static final int PORT = 8080;

  private final Dao dao;

  public Client(String host, int port) throws IOException {
    this.dao = new Dao(new Socket(host, port));
    logger.info("Клиент успешно создан");
  }

  /**
   * Метод запускает клиент.
   *
   * @throws IOException Неудачая попытка чтения/записи
   */
  public static void main(String[] args) throws IOException {
    var client = new Client(HOST, PORT);
    var request =
        new StartGameRequestDto("Sergei", GameMode.HUMAN_VS_HUMAN, ChessType.CLASSIC, Color.WHITE);

    // Здесь должен быть запуск UI и взаимодействие через него
    var response = client.dao.query(request, StartGameResponseDto.class);
    System.out.println(response);
  }
}
