package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerGame implements Runnable {
  private final ServerPlayer playerA;
  private final ServerPlayer playerB;
  private final Board board;

  /** Конструктор для объектов типа ServerGame. */
  public ServerGame(ServerPlayer playerA, ServerPlayer playerB, Board board) {
    this.playerA = playerA;
    this.playerB = playerB;
    this.board = board;

    log.info("Игра создана");
  }

  @Override
  public void run() {
    log.info("Игра запущенна");
    
    try {
      close();
    } catch (IOException e) {
      log.error("Во время закрытия соединения возникли проблемы - " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Функция закрывает ресурсы.
   *
   * @throws IOException В случае ошибки закрытия
   */
  public void close() throws IOException {
    playerA.socket().close();
    playerB.socket().close();

    log.info("Соединение успешно закрыто!");
  }
}
