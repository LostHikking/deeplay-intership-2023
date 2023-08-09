package io.deeplay.grandmastery;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerGame implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(ServerGame.class);

  private final ServerPlayer playerA;
  private final ServerPlayer playerB;

  /** Конструктор для объектов типа ServerGame. */
  public ServerGame(ServerPlayer playerA, ServerPlayer playerB) {
    this.playerA = playerA;
    this.playerB = playerB;

    logger.info("Игра созданна");
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
   * Функция закрывает ресуры.
   *
   * @throws IOException В случае ошибки закрытия
   */
  public void close() throws IOException {
    playerA.socket().close();
    playerB.socket().close();

    logger.info("Соединение успешно закрыто!");
  }
}
