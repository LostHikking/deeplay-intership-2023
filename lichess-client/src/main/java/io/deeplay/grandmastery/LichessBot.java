package io.deeplay.grandmastery;

import chariot.Client;
import java.io.IOException;
import java.util.Properties;

public class LichessBot implements Runnable {
  private final LichessClient client =
      new LichessClient(Client.auth(System.getenv("LICHESS_API_KEY")));
  private final String botName;

  /**
   * Конструктор.
   *
   * @throws RuntimeException Ошибка чтения из конфига
   */
  public LichessBot() {
    try (var config = LichessBot.class.getClassLoader().getResourceAsStream("config.properties")) {
      var properties = new Properties();
      properties.load(config);

      botName = properties.getProperty("bot-name");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    var eventHandler = new EventHandler(client, botName);
    Runtime.getRuntime().addShutdownHook(new Thread(eventHandler::clean));

    eventHandler.start();
  }

  public static void main(String[] args) {
    new LichessBot().run();
  }
}
