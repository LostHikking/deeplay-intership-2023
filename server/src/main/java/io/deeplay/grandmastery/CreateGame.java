package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.Server.GAMES;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateGame implements Runnable {
  private static final Object MUTEX = new Object();
  public static final List<ServerPlayer> players = new ArrayList<>();

  private final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;

  /**
   * Конструктор.
   *
   * @param socket Socket
   * @param in BufferedReader
   * @param out BufferedWriter
   */
  public CreateGame(Socket socket, BufferedReader in, BufferedWriter out) {
    this.socket = socket;
    this.in = in;
    this.out = out;
  }

  @Override
  public void run() {
    try {
      var startJson = ServerDao.getJsonFromClient(in);
      var requestDto = ConversationService.deserialize(startJson, StartGameRequest.class);

      var serverGame = createServerGame(requestDto);
      if (serverGame != null) {
        GAMES.execute(serverGame);
      }
    } catch (Exception e) {
      log.error("В таске CreateGame возникла ошибка - " + e.getMessage());
      throw new IllegalStateException();
    }
  }

  /**
   * Метод создаёт игру по StartGameRequest.
   *
   * @param request Запрос о начале игры.
   * @return Игру.
   * @throws IllegalArgumentException неизвестный GameMode.
   */
  public ServerGame createServerGame(StartGameRequest request) {
    if (request.getNameBotOne() != null && request.getNameBotTwo() != null) {
      var firstPlayer =
          new FarmPlayer(request.getNameBotOne(), Color.WHITE, request.getChessType());
      var secondPlayer =
          new FarmPlayer(request.getNameBotTwo(), Color.BLACK, request.getChessType());

      log.info("Создали игру BOT_VS_BOT");
      return new ServerGame(firstPlayer, secondPlayer, request.getChessType(), socket);
    } else if (request.getNameBotOne() != null) {
      var player1 =
          new ServerPlayer(
              socket, in, out, request.getPlayerName(), request.getColor(), request.getChessType());

      var otherColor = request.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE;
      var player2 = new FarmPlayer(request.getNameBotOne(), otherColor, request.getChessType());

      log.info("Создали игру HUMAN_VS_BOT");
      return new ServerGame(player1, player2, request.getChessType(), null);
    } else {
      synchronized (MUTEX) {
        var player1 =
            new ServerPlayer(
                socket,
                in,
                out,
                request.getPlayerName(),
                request.getColor(),
                request.getChessType());

        var player2 = hasMatch(player1);
        if (player2 != null) {
          players.remove(player2);
          log.info("Создали игру HUMAN_VS_HUMAN");

          return new ServerGame(player1, player2, request.getChessType(), null);
        } else {
          players.add(player1);
          return null;
        }
      }
    }
  }

  /**
   * Метод сопоставляет пользователей по определённым парамерам.
   *
   * @param player Игрок.
   * @return Игрока, если есть совпадение, иначе null
   */
  public ServerPlayer hasMatch(ServerPlayer player) {
    deleteClientsWithoutConnection();

    return players.stream()
        .filter(
            pl -> pl.getColor() != player.getColor() && pl.getChessType() == player.getChessType())
        .findAny()
        .orElse(null);
  }

  public void deleteClientsWithoutConnection() {
    players.removeIf(serverPlayer -> serverPlayer.getSocket().isClosed());
  }

  public void clearPlayers() {
    players.clear();
  }
}
