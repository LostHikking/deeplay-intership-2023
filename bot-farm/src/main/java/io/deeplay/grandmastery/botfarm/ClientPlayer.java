package io.deeplay.grandmastery.botfarm;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.CreateMoveFarmRequest;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ClientPlayer extends Player {
  private Socket socket;
  private BufferedReader in;
  private BufferedWriter out;

  /**
   * Конструктор.
   *
   * @param socket Сокет
   * @param in BufferedReader
   * @param out BufferedWriter
   * @param otherColor цвет
   */
  public ClientPlayer(Socket socket, BufferedReader in, BufferedWriter out, Color otherColor) {
    super("client", otherColor);
    this.socket = socket;
    this.in = in;
    this.out = out;
  }

  @Override
  public Move createMove() throws GameException {
    try {
      var data = in.readLine();
      log.info("Получили данные - " + data);

      return ConversationService.deserialize(data, CreateMoveFarmRequest.class).getLastMove();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }
}
