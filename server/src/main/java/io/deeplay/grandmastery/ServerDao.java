package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.StartGameResponse;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ServerDao(Player playerOne, Player playerTwo, Socket socket) {

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

  /**
   * Функция уведомляет о неправильном ходе.
   * @param color Цвет.
   * @throws IOException Ошибка ввода/вывода
   * */
  public void notifyWrongMove(Color color) throws IOException {
    var json = ConversationService.serialize(new WrongMove());

    if (playerOne.getColor() == color && playerOne instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    } else if (playerTwo.getColor() == color && playerTwo instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    }
  }

  /**
   * Функция уведомляет о совершённом ходе.
   * @param color Цвет.
   * @param lastMove Ход.
   * @throws IOException Ошибка ввода/вывода
   * */
  public void notifySuccessMove(Color color, Move lastMove) throws IOException {
    var json = ConversationService.serialize(new AcceptMove(lastMove));

    if (playerOne.getColor() != color && playerOne instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    } else if (playerTwo.getColor() != color && playerTwo instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    }
  }

  /**
   * Функция уведомляет о начале игры.
   * @param board Доска.
   * @throws IOException Ошибка ввода/вывода
   * */
  public void notifyStartGame(Board board) throws IOException {
    var startGameResponse = new StartGameResponse(Boards.getStringFromBoard(board));
    var json = ConversationService.serialize(startGameResponse);

    if (playerOne instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    }
    if (playerTwo instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    }
  }

  /**
   * Функция возвращает результат игры.
   * @param gameStatus Статус игры.
   * @param boardList Список досок.
   * @throws IOException Ошибка ввода/вывода
   * */
  public void sendResult(GameState gameStatus, List<Board> boardList) throws IOException {
    var boards = new ArrayList<String>();
    for (Board board : boardList) {
      boards.add(Boards.getStringFromBoard(board));
    }

    var result = new ResultGame(gameStatus, boards);
    var json = ConversationService.serialize(result);

    if (playerOne instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    }
    if (playerTwo instanceof ServerPlayer serverPlayer) {
      ServerDao.send(serverPlayer.getOut(), json);
    }
    if (socket != null) {
      try (var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8))) {
        ServerDao.send(out, json);
      }
    }
  }

  /**
   * Функция закрывает соединение.
   * @throws IOException Ошибка ввода/вывода
   * */
  public void close() throws IOException {
    if (playerOne instanceof ServerPlayer serverPlayer) {
      serverPlayer.getSocket().close();
    }
    if (playerTwo instanceof ServerPlayer serverPlayer) {
      serverPlayer.getSocket().close();
    }
    if (socket != null) {
      socket.close();
    }

    log.info("Соединение успешно закрыто!");
  }
}
