package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.CreateFarmGameRequest;
import io.deeplay.grandmastery.dto.CreateFarmGameResponse;
import io.deeplay.grandmastery.dto.CreateMoveFarmRequest;
import io.deeplay.grandmastery.dto.CreateMoveFarmResponse;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.exceptions.QueryException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class FarmPlayer extends Player {
  private static final String HOST = "localhost";
  private static final int PORT = 2023;

  private final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;
  private final ChessType chessType;

  /**
   * Конструктор с параметрами.
   *
   * @param name Имя
   * @param color Цвет
   * @param chessType Тип шахмат
   * @throws IllegalStateException Ошибка создания FarmPlayer
   */
  public FarmPlayer(String name, Color color, ChessType chessType) {
    super(name, color);
    try {
      socket = new Socket(HOST, PORT);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
      this.chessType = chessType;

      log.info("Создали FarmPlayer - " + name);
    } catch (IOException e) {
      log.error("Ошибка при создании игрока - " + name);
      throw new IllegalStateException(e);
    }
  }

  /**
   * Метод инициализирует FarmPlayer.
   *
   * @param board Доска
   * @throws QueryException Ошибка при отправке/получении данных
   * @throws IllegalStateException Ошибка при создании бота на фабрике
   */
  public void init(Board board) throws QueryException {
    var status =
        query(
                new CreateFarmGameRequest(name, color, chessType, Boards.getStringFromBoard(board)),
                CreateFarmGameResponse.class)
            .getStatus();
    if (!"OK".equals(status)) {
      throw new IllegalStateException("Bot not found");
    }
  }

  @Override
  public Move createMove() throws GameException {
    try {
      if (gameHistory.isEmpty()) {
        var response = in.readLine();
        log.info("Получили данные - " + response);
        return ConversationService.deserialize(response, CreateMoveFarmResponse.class).getMove();
      }

      var dto = new CreateMoveFarmRequest(this.gameHistory.getLastMove());

      return query(dto, CreateMoveFarmResponse.class).getMove();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }

  /**
   * Функция отправляет запрос на сервер и возвращает ответ от него.
   *
   * @param dto Request Dto
   * @return Response Dto
   * @throws QueryException Ошибка выполнения запроса
   */
  public <T extends IDto> T query(IDto dto, Class<T> clazz) throws QueryException {
    try {
      var json = ConversationService.serialize(dto);
      send(json);

      var response = in.readLine();
      log.info("Получили данные - " + response);

      return ConversationService.deserialize(response, clazz);
    } catch (IOException e) {
      throw new QueryException(e.getMessage());
    }
  }

  /**
   * Метод отправляет данные в BufferedWriter.
   *
   * @param json Данные
   * @throws IOException Ошибка при отправке данных
   */
  public void send(String json) throws IOException {
    log.info("Отправили данные - " + json);
    out.write(json);
    out.newLine();
    out.flush();
  }
}
