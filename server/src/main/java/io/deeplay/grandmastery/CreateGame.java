package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.Server.GAMES;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.StartGameRequestDto;
import io.deeplay.grandmastery.dto.StartGameResponseDto;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateGame implements Runnable {
  private static final Object MUTEX = new Object();
  private static final Logger logger = LoggerFactory.getLogger(CreateGame.class);
  public static final List<ServerPlayer> players = new ArrayList<>();

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
        case BOT_VS_BOT -> {
          var firstPlayer = new AiPlayer(Color.WHITE);
          var secondPlayer = new AiPlayer(Color.BLACK);

          var gameController = new GameController(firstPlayer, secondPlayer);
          gameController.beginPlay(requestDto.getChessType());

          while (!gameController.isGameOver()) {
            try {
              gameController.nextMove();
            } catch (GameException e) {
              logger.error(e.getMessage());
            }
          }

          var stringBoard = Boards.getStringFromBoard(gameController.getBoard());
          var responseDto1 = new StartGameResponseDto(stringBoard);
          var returnJson1 = ConversationService.serialize(responseDto1);

          send(out, returnJson1);
          socket.close();
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
          var player2 =
              new ServerPlayer(
                  null,
                  null,
                  null,
                  "AI",
                  otherColor,
                  requestDto.getGameMode(),
                  requestDto.getChessType());

          var board = getBoardByChessType(player1.chessType());
          var stringBoard = Boards.getStringFromBoard(board);

          var responseDto1 = new StartGameResponseDto(stringBoard);
          var returnJson1 = ConversationService.serialize(responseDto1);

          send(player1.out(), returnJson1);
          GAMES.execute(new ServerGame(player1, player2, board));
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
              var board = getBoardByChessType(player1.chessType());
              var stringBoard = Boards.getStringFromBoard(board);

              var responseDto1 = new StartGameResponseDto(stringBoard);
              var returnJson1 = ConversationService.serialize(responseDto1);

              var responseDto2 = new StartGameResponseDto(stringBoard);
              var returnJson2 = ConversationService.serialize(responseDto2);

              send(player1.out(), returnJson1);
              send(player2.out(), returnJson2);

              players.remove(player2);
              GAMES.execute(new ServerGame(player1, player2, board));
            } else {
              players.add(player1);
            }
          }
        }
        default -> throw new RuntimeException("Неизвестный GameMode " + requestDto.getGameMode());
      }
    } catch (Exception e) {
      logger.error("В таске CreateGame возникла ошибка - " + e.getMessage());
      throw new RuntimeException();
    }
  }

  private Board getBoardByChessType(ChessType chessType) {
    var board = new HashBoard();

    if (chessType == ChessType.CLASSIC) {
      Boards.defaultChess().accept(board);
    } else {
      Boards.fischerChess().accept(board);
    }

    return board;
  }

  private ServerPlayer hasMatch(ServerPlayer player) {
    return players.stream()
        .filter(
            pl ->
                pl.color() != player.color()
                    && pl.gameMode() == player.gameMode()
                    && pl.chessType() == player.chessType())
        .findAny()
        .orElse(null);
  }

  private void send(BufferedWriter bufferedWriter, String text) throws IOException {
    bufferedWriter.write(text);
    bufferedWriter.newLine();
    bufferedWriter.flush();

    logger.info("Отправили данные на клиент - " + text);
  }
}
