package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.dto.StartGameResponse;
import io.deeplay.grandmastery.dto.WaitAnswerDraw;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.QueryException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
  private static final String HOST = "localhost";
  private static final int PORT = 8080;

  private final GameHistory gameHistory = new GameHistory();
  private final ClientController clientController;
  private Player player;

  /**
   * Конструктор для класса Client.
   *
   * @param host Адрес хоста
   * @param port Порт
   * @param ui UI
   * @throws IOException Ошибка ввода/вывода
   */
  public Client(String host, int port, UI ui) throws IOException {
    var socket = new Socket(host, port);
    var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
    var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

    this.clientController = new ClientController(new ClientDao(socket, in, out), ui);
    log.info("Клиент успешно создан");
  }

  /**
   * Запускает клиент с определённым UI.
   *
   * @throws IOException ошибка ввода/вывода
   * @throws IllegalStateException неизвестный тип ответа от сервера
   */
  public void run() throws IOException, IllegalStateException {
    GameMode gameMode = clientController.selectMode();
    if (gameMode == GameMode.BOT_VS_BOT) {
      botVsBot(clientController.selectChessType());
      return;
    }

    Color color = clientController.selectColor();
    String name = clientController.inputPlayerName(color);
    ChessType chessType = clientController.selectChessType();

    player = new HumanPlayer(name, color, clientController.ui());
    StartGameRequest request = new StartGameRequest(name, gameMode, chessType, color);
    IDto response = clientController.query(request);

    if (response instanceof StartGameResponse responseDto) {
      Board board = Boards.getBoardFromString(responseDto.getBoard());
      gameHistory.startup(board);
      player.startup(board);

      startGame();
    } else {
      throw new IllegalStateException("Неизвестный тип ответа от сервера - " + response);
    }
  }

  /**
   * Запускает и возвращает результат игр bot_vs_bot.
   *
   * @throws QueryException ошибка во время запроса.
   * @throws IllegalStateException неизвестный тип ответа от сервера.
   */
  private void botVsBot(ChessType chessType) throws QueryException, IllegalStateException {
    StartGameRequest request =
        new StartGameRequest("AI", GameMode.BOT_VS_BOT, chessType, Color.WHITE);
    IDto response = clientController.query(request);

    if (response instanceof ResultGame resultGame) {
      var boards = resultGame.getBoards();
      var stringBoard = boards.get(boards.size() - 1);

      clientController.showBoard(Boards.getBoardFromString(stringBoard), Color.WHITE);
      clientController.showResultGame(resultGame.getGameState());
    } else {
      throw new IllegalStateException("Неизвестный тип ответа от сервера - " + response);
    }
  }

  private void startGame() throws IOException {
    IDto serverDto;

    do {
      String serverResponse = clientController.getJsonFromServer();
      serverDto = ConversationService.deserialize(serverResponse);

      if (serverDto instanceof WaitMove) {
        String json = ConversationService.serialize(new SendMove(makeMove()));
        clientController.send(json);
      } else if (serverDto instanceof WrongMove) {
        gameHistory.getBoards().remove(gameHistory.getBoards().size() - 1);
        player.startup(gameHistory.getCurBoard());
      } else if (serverDto instanceof AcceptMove acceptMove) {
        player.makeMove(acceptMove.getMove());
        gameHistory.addBoard(player.getBoard());
      } else if (serverDto instanceof WaitAnswerDraw) {
        String json = ConversationService.serialize(new SendAnswerDraw(player.answerDraw()));
        clientController.send(json);
      }
    } while (!(serverDto instanceof ResultGame));

    player.gameOver(((ResultGame) serverDto).getGameState());
    gameHistory.gameOver(((ResultGame) serverDto).getGameState());
    clientController.close();
  }

  private Move makeMove() {
    while (true) {
      Move move = player.createMove();
      if (move.moveType() == MoveType.DEFAULT) {
        player.makeMove(move);
        gameHistory.addBoard(player.getBoard());
      }

      if (player.getLastMove() != null) {
        return move;
      }
    }
  }

  /**
   * Метод запускает клиент.
   *
   * @throws IOException Неудачная попытка чтения/записи
   */
  public static void main(String[] args) throws IOException {
    var ui = new Gui();
    new Client(HOST, PORT, ui).run();
  }
}
