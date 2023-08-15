package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.Server.GAMES;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateGame implements Runnable {
  private static final Object MUTEX = new Object();
  public static final List<ServerPlayer> players = new ArrayList<>();

  private final Socket socket;

  public CreateGame(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
      var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

      var startJson = ServerUtils.getJsonFromClient(in);
      var requestDto = ConversationService.deserialize(startJson, StartGameRequest.class);

      switch (requestDto.getGameMode()) {
        case BOT_VS_BOT -> {
          var firstPlayer = new AiPlayer(Color.WHITE);
          var secondPlayer = new AiPlayer(Color.BLACK);

          GAMES.execute(
              new ServerGame(firstPlayer, secondPlayer, requestDto.getChessType(), socket));
          log.info("Создали игру BOT_VS_BOT");
        }
        case HUMAN_VS_BOT -> {
          var player1 =
              new ServerPlayer(
                  socket,
                  in,
                  out,
                  requestDto.getPlayerName(),
                  requestDto.getColor(),
                  requestDto.getGameMode(),
                  requestDto.getChessType());

          var otherColor = requestDto.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE;
          var player2 = new AiPlayer(otherColor);

          GAMES.execute(new ServerGame(player1, player2, requestDto.getChessType(), null));
          log.info("Создали игру HUMAN_VS_BOT");
        }
        case HUMAN_VS_HUMAN -> {
          synchronized (MUTEX) {
            var player1 =
                new ServerPlayer(
                    socket,
                    in,
                    out,
                    requestDto.getPlayerName(),
                    requestDto.getColor(),
                    requestDto.getGameMode(),
                    requestDto.getChessType());

            var player2 = hasMatch(player1);
            if (player2 != null) {
              players.remove(player2);
              GAMES.execute(new ServerGame(player1, player2, requestDto.getChessType(), null));
              log.info("Создали игру HUMAN_VS_HUMAN");
            } else {
              players.add(player1);
            }
          }
        }
        default -> throw new RuntimeException("Неизвестный GameMode " + requestDto.getGameMode());
      }
    } catch (Exception e) {
      log.error("В таске CreateGame возникла ошибка - " + e.getMessage());
      throw new RuntimeException();
    }
  }

  private ServerPlayer hasMatch(ServerPlayer player) {
    return players.stream()
        .filter(
            pl ->
                pl.getColor() != player.getColor()
                    && pl.getGameMode() == player.getGameMode()
                    && pl.getChessType() == player.getChessType())
        .findAny()
        .orElse(null);
  }
}
