package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.Server.GAMES;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.dto.StartGameRequestDto;
import io.deeplay.grandmastery.dto.StartGameResponseDto;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateGame implements Runnable {
  private static final Object MUTEX = new Object();
  private static final Logger logger = LoggerFactory.getLogger(CreateGame.class);
  private static ServerPlayer serverPlayer;

  private final Socket socket;

  public CreateGame(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      logger.info("Запущен таск CreateGame");

      var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
      var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

      var startJson = in.readLine();
      var requestDto = ConversationService.deserialize(startJson, StartGameRequestDto.class);

      switch (requestDto.getGameMode()) {
        case BOT_VS_BOT, HUMAN_VS_BOT -> throw new RuntimeException();
        case HUMAN_VS_HUMAN -> {
          synchronized (MUTEX) {
            if (serverPlayer == null) {
              serverPlayer =
                  new ServerPlayer(
                      socket, in, out, requestDto.getPlayerName(), requestDto.getColor());
            } else {
              var player =
                  new ServerPlayer(
                      socket, in, out, requestDto.getPlayerName(), requestDto.getColor());

              var responseDto = new StartGameResponseDto(1, 1, "1", "1");
              var returnJson = ConversationService.serialize(responseDto);

              send(out, returnJson);
              send(serverPlayer.out(), returnJson);

              var game = new ServerGame(serverPlayer, player);
              serverPlayer = null;

              GAMES.execute(game);
            }
          }
        }
        default -> throw new RuntimeException("Неизвестный GameMode " + requestDto.getGameMode());
      }
    } catch (Exception e) {
      logger.error("В таске CreateGame возникла ошибка");
      throw new RuntimeException();
    }
  }

  private void send(BufferedWriter bufferedWriter, String text) throws IOException {
    bufferedWriter.write(text);
    bufferedWriter.newLine();
    bufferedWriter.flush();

    logger.info("Отправили данные на клиент - " + text);
  }
}
