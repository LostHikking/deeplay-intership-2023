package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerGame implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(ServerGame.class);

  private final ServerPlayer playerA;
  private final ServerPlayer playerB;
  private final Board board;

  /** Конструктор для объектов типа ServerGame. */
  public ServerGame(ServerPlayer playerA, ServerPlayer playerB, Board board) {
    this.playerA = playerA;
    this.playerB = playerB;
    this.board = board;

    logger.info("Игра создана");
  }

  @Override
  public void run() {
    logger.info("Игра запущенна");
    
    try {
      close();
    } catch (IOException e) {
      logger.error("Во время закрытия соединения возникли проблемы - " + e.getMessage());
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

    logger.info("Соединение успешно закрыто!");
  }
}
