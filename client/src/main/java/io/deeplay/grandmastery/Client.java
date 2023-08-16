package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Game;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.dto.StartGameResponse;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.ui.ConsoleUi;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.IOException;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
  private static final String HOST = "localhost";
  private static final int PORT = 8080;
  private final Dao dao;
  private final UI ui;
  private final GameHistory gameHistory = new GameHistory();
  private final Game game = new Game();

  /**
   * Конструктор для класса Client.
   *
   * @param host Адрес хоста
   * @param port Порт
   * @param ui UI
   * @throws IOException Ошибка ввода/вывода
   */
  public Client(String host, int port, UI ui) throws IOException {
    this.dao = new Dao(new Socket(host, port));
    this.ui = ui;

    log.info("Клиент успешно создан");
  }

  /**
   * Запускает клиент с определённым UI.
   *
   * @throws IOException ошибка ввода/вывода
   * @throws RuntimeException неизвестный тип ответа от сервера
   */
  public void run() throws IOException {
    var gameMode = ui.selectMode();
    var isBotVsBotGame = gameMode == GameMode.BOT_VS_BOT;

    var color = isBotVsBotGame ? Color.WHITE : ui.selectColor();
    var name = isBotVsBotGame ? "AI" : ui.inputPlayerName(color);
    var chessType = ui.selectChessType();

    var request = new StartGameRequest(name, gameMode, chessType, color);

    var response = dao.query(request);

    if (response instanceof ResultGame resultGame) {
      var boards = resultGame.getBoards();
      var stringBoard = boards.get(boards.size() - 1);

      ui.showBoard(Boards.getBoardFromString(stringBoard), color);
      ui.showResultGame(resultGame.getGameState());
    } else if (response instanceof StartGameResponse responseDto) {
      var board = Boards.getBoardFromString(responseDto.getBoard());
      gameHistory.startup(board);
      game.startup(board);

      ui.printHelp();
      ui.showBoard(gameHistory.getCurBoard(), color);

      startGame(color, name);
    } else {
      throw new RuntimeException("Неизвестный тип ответа от сервера - " + response);
    }
  }

  private void startGame(Color color, String name) throws IOException {
    IDto serverDto;

    do {
      var serverResponse = dao.getJsonFromServer();
      serverDto = ConversationService.deserialize(serverResponse);

      if (serverDto instanceof WaitMove) {
        makeMove(name);
        ui.showBoard(gameHistory.getCurBoard(), color);
      } else if (serverDto instanceof WrongMove) {
        gameHistory.getBoards().remove(gameHistory.getBoards().size() - 1);
        game.startup(gameHistory.getCurBoard());
        game.setColorMove(color);

        ui.showBoard(gameHistory.getCurBoard(), color);
        ui.incorrectMove();
      } else if (serverDto instanceof AcceptMove acceptMove) {
        game.makeMove(acceptMove.getMove());
        gameHistory.addBoard(game.getCopyBoard());

        ui.showBoard(gameHistory.getCurBoard(), color);
      }
    } while (!(serverDto instanceof ResultGame));

    game.gameOver();
    gameHistory.gameOver();

    ui.showResultGame(((ResultGame) serverDto).getGameState());
    dao.close();
  }

  private void makeMove(String name) throws IOException {
    while (true) {
      try {
        var move = LongAlgebraicNotation.getMoveFromString(ui.inputMove(name));
        game.makeMove(move);

        var json = ConversationService.serialize(new SendMove(move));
        dao.send(json);

        gameHistory.addBoard(game.getCopyBoard());
        break;
      } catch (GameException e) {
        ui.incorrectMove();
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
