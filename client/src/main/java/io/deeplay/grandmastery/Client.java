package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Game;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.UI;
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
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.ui.ConsoleUi;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
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
  private final Game game = new Game();

  private final ClientController clientController;

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
  public void run() throws IOException {
    var gameMode = clientController.selectMode();
    var isBotVsBotGame = gameMode == GameMode.BOT_VS_BOT;

    var color = isBotVsBotGame ? Color.WHITE : clientController.selectColor();
    var name = isBotVsBotGame ? "AI" : clientController.inputPlayerName(color);
    var chessType = clientController.selectChessType();

    var request = new StartGameRequest(name, gameMode, chessType, color);

    var response = clientController.query(request);

    if (response instanceof ResultGame resultGame) {
      var boards = resultGame.getBoards();
      var stringBoard = boards.get(boards.size() - 1);

      clientController.showBoard(Boards.getBoardFromString(stringBoard), color);
      clientController.showResultGame(resultGame.getGameState());
    } else if (response instanceof StartGameResponse responseDto) {
      var board = Boards.getBoardFromString(responseDto.getBoard());
      gameHistory.startup(board);
      game.startup(board);

      clientController.printHelp();
      clientController.showBoard(gameHistory.getCurBoard(), color);

      startGame(color, name);
    } else {
      throw new IllegalStateException("Неизвестный тип ответа от сервера - " + response);
    }
  }

  private void startGame(Color color, String name) throws IOException {
    IDto serverDto;

    do {
      var serverResponse = clientController.getJsonFromServer();
      serverDto = ConversationService.deserialize(serverResponse);

      if (serverDto instanceof WaitMove) {
        makeMove(name);
        clientController.showBoard(gameHistory.getCurBoard(), color);
      } else if (serverDto instanceof WrongMove) {
        gameHistory.getBoards().remove(gameHistory.getBoards().size() - 1);
        game.startup(gameHistory.getCurBoard());
        game.setColorMove(color);

        clientController.showBoard(gameHistory.getCurBoard(), color);
        clientController.incorrectMove();
      } else if (serverDto instanceof AcceptMove acceptMove) {
        game.makeMove(acceptMove.getMove());
        gameHistory.addBoard(game.getCopyBoard());

        clientController.showBoard(gameHistory.getCurBoard(), color);
      } else if (serverDto instanceof WaitAnswerDraw) {
        String json =
            ConversationService.serialize(new SendAnswerDraw(clientController.answerDraw()));
        clientController.send(json);
      }
    } while (!(serverDto instanceof ResultGame));

    game.gameOver();
    gameHistory.gameOver();

    clientController.showResultGame(((ResultGame) serverDto).getGameState());
    clientController.close();
  }

  private void makeMove(String name) throws IOException {
    while (true) {
      try {
        String input = clientController.inputMove(name);
        Move move;
        if (("sur".equalsIgnoreCase(input) || "surrender".equalsIgnoreCase(input))
            && clientController.confirmSur()) {
          move = new Move(null, null, null, MoveType.SURRENDER);
        } else if ("draw".equalsIgnoreCase(input)) {
          move = new Move(null, null, null, MoveType.DRAW_OFFER);
        } else {
          move = LongAlgebraicNotation.getMoveFromString(input);
          game.makeMove(move);
          gameHistory.addBoard(game.getCopyBoard());
        }

        var json = ConversationService.serialize(new SendMove(move));
        clientController.send(json);
        break;
      } catch (GameException e) {
        clientController.incorrectMove();
      }
    }
  }

  /**
   * Метод запускает клиент.
   *
   * @throws IOException Неудачная попытка чтения/записи
   */
  public static void main(String[] args) throws IOException {
    new Client(HOST, PORT, new ConsoleUi(System.in, System.out)).run();
  }
}
