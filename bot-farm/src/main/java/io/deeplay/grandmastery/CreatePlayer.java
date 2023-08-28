package io.deeplay.grandmastery;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.CreateFarmGameRequest;
import io.deeplay.grandmastery.dto.CreateFarmGameResponse;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.BotUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreatePlayer implements Runnable {
  private final Socket socket;
  @Getter private final BufferedReader in;
  @Getter private final BufferedWriter out;

  /**
   * Конструктор.
   *
   * @param socket Сокет
   * @param in BufferedReader
   * @param out BufferedWriter
   */
  public CreatePlayer(Socket socket, BufferedReader in, BufferedWriter out) {
    this.socket = socket;
    this.in = in;
    this.out = out;
  }

  @Override
  public void run() {
    try {
      var req = ConversationService.deserialize(in.readLine(), CreateFarmGameRequest.class);
      var playerClass = BotFarm.playerList.get(req.getName());
      var constructor = playerClass.getConstructor(Color.class);
      var player = constructor.newInstance(req.getColor());

      log.info(player.getName());
      log.info(player.getColor().name());

      var json = ConversationService.serialize(new CreateFarmGameResponse("OK"));
      BotUtils.send(out, json);

      BotFarm.PLAYERS.execute(
          new RunGame(
              player,
              new ClientPlayer(socket, in, out, player.getColor().getOpposite()),
              Boards.getBoardFromString(req.getBoard())));
    } catch (IOException
        | NoSuchMethodException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
