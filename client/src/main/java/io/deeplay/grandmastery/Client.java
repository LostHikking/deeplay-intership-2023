package io.deeplay.grandmastery;

import com.sun.tools.javac.Main;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendBoard;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.dto.StartGameResponse;
import io.deeplay.grandmastery.dto.WaitAnswerDraw;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.exceptions.QueryException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.ui.ConsoleUi;
import io.deeplay.grandmastery.utils.Boards;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Client {
  private static final ExecutorService MAKE_MOVE = Executors.newSingleThreadExecutor();
  private static final AtomicReference<IOException> IO_EXCEPTION_REF = new AtomicReference<>();

  protected final String host;
  protected final int port;
  protected ClientController clientController;
  protected Player player;
  protected boolean reconnect;

  /**
   * Конструктор для класса Client.
   *
   * @param ui UI
   * @throws Exception в случаи ошибки с открытием/чтением конфига.
   */
  public Client(UI ui) throws Exception {
    try (InputStream config =
        Main.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties properties = new Properties();
      properties.load(config);

      host = properties.getProperty("host");
      port = Integer.parseInt(properties.getProperty("port"));
    }

    this.clientController = new ClientController(ui);
    clientController.connect(host, port);
    reconnect = false;
  }

  /**
   * Метод для переподключения клиента к серверу.
   *
   * @throws IOException При проблемах с закрытием текущего соединения или при установке нового.
   */
  protected void reconnect() throws Exception {
    clientController.close();
    reconnect = true;
    clientController.connect(host, port);
  }

  private static boolean isServerUnavailable(IOException e) {
    return e.getMessage().contains("Server disconnect");
  }

  /**
   * Запускает клиент с определённым UI.
   *
   * @throws IOException ошибка при закрытии потоков ввода/вывода
   * @throws IllegalStateException неизвестный тип ответа от сервера
   * @throws Exception другая ошибка.
   */
  public void run() throws Exception {
    do {
      try {
        GameMode gameMode = clientController.selectMode();
        if (gameMode == GameMode.BOT_VS_BOT) {
          botVsBot(clientController.selectChessType());
        } else {
          Color color = clientController.selectColor();
          String name = clientController.inputPlayerName(color);
          ChessType chessType = clientController.selectChessType();

          player = new HumanPlayer(name, color, clientController.getUi());
          StartGameRequest request =
              gameMode == GameMode.HUMAN_VS_BOT
                  ? new StartGameRequest(
                      name, chessType, color, clientController.selectBot(color.getOpposite()), null)
                  : new StartGameRequest(name, chessType, color, null, null);
          IDto response = clientController.query(request);

          if (response instanceof StartGameResponse responseDto) {
            Board board = Boards.fromString(responseDto.getBoard());
            player.startup(board);

            startGame();
          } else {
            MAKE_MOVE.shutdown();
            clientController.close();
            throw new IllegalStateException("Неизвестный тип ответа от сервера - " + response);
          }
        }
      } catch (IOException e) {
        if (isServerUnavailable(e)) {
          log.error("Соединение с сервером разорвано");
          log.info("Попытка переподключения: ");

          reconnect();
        } else {
          throw e;
        }
      }
    } while (reconnect);

    clientController.close();
  }

  /**
   * Запускает и возвращает результат игр bot_vs_bot.
   *
   * @throws QueryException ошибка во время запроса.
   * @throws IllegalStateException неизвестный тип ответа от сервера.
   */
  private void botVsBot(ChessType chessType) throws IOException, IllegalStateException {
    String firstBot = clientController.selectBot(Color.WHITE);
    String secondBot = clientController.selectBot(Color.BLACK);

    StartGameRequest request =
        new StartGameRequest("AI", chessType, Color.WHITE, firstBot, secondBot);
    IDto response = clientController.query(request);

    while (response instanceof SendBoard sendBoard) {
      clientController.showBoard(Boards.fromString(sendBoard.getBoard()), Color.WHITE);
      response = ConversationService.deserialize(clientController.getJsonFromServer());
    }

    if (response instanceof ResultGame resultGame) {
      var boards = resultGame.getBoards();
      var stringBoard = boards.get(boards.size() - 1);

      clientController.showBoard(Boards.fromString(stringBoard), Color.WHITE);
      clientController.showResultGame(resultGame.getGameState());
      reconnect = false;
    } else {
      MAKE_MOVE.shutdown();
      clientController.close();
      throw new IllegalStateException("Неизвестный тип ответа от сервера - " + response);
    }
  }

  private void startGame() throws IOException, GameException {
    IDto serverDto;

    do {
      String serverResponse = clientController.getJsonFromServer();
      serverDto = ConversationService.deserialize(serverResponse);

      if (IO_EXCEPTION_REF.get() != null) {
        throw IO_EXCEPTION_REF.get();
      }

      if (serverDto instanceof WaitMove) {
        if (!MAKE_MOVE.isShutdown()) {
          MAKE_MOVE.execute(this::makeMove);
        } else {
          throw GameErrorCode.GAME_ALREADY_OVER.asException();
        }
      } else if (serverDto instanceof WrongMove) {
        player.rollback();
      } else if (serverDto instanceof AcceptMove acceptMove) {
        player.makeMove(acceptMove.getMove());
      } else if (serverDto instanceof WaitAnswerDraw) {
        String json = ConversationService.serialize(new SendAnswerDraw(player.answerDraw()));
        clientController.send(json);
      }
    } while (!(serverDto instanceof ResultGame));

    MAKE_MOVE.shutdown();
    player.gameOver(((ResultGame) serverDto).getGameState());
    reconnect = false;
  }

  private void makeMove() {
    Move move;
    while (true) {
      try {
        move = player.createMove();
        if (move != null) {
          if (move.moveType() == MoveType.DEFAULT) {
            player.makeMove(move);
          }
          break;
        }
      } catch (GameException e) {
        if (e.getMessage().contains(GameErrorCode.GAME_ALREADY_OVER.getDescription())) {
          log.error("Игра уже завершилась!");
          clientController.printEventMessage("Игра уже завершилась!");
          return;
        }
      }
    }

    try {
      String json = ConversationService.serialize(new SendMove(move));
      clientController.send(json);
    } catch (IOException e) {
      IO_EXCEPTION_REF.set(e);
    }
  }

  /** Метод запускает клиент. */
  public static void main(String[] args) {
    try {
      UI ui = createUi(UI.getUiFromConfig());
      new Client(ui).run();
    } catch (Exception e) {
      log.error("Ошибка во время работы клиента", e);
    } finally {
      if (!MAKE_MOVE.isShutdown()) {
        MAKE_MOVE.shutdown();
      }
    }
  }

  protected static UI createUi(String uiName) {
    return switch (uiName) {
      case "gui" -> new Gui(true);
      case "tui" -> new ConsoleUi(System.in, System.out);
      default -> throw new IllegalArgumentException("Неизвестный ui: " + uiName);
    };
  }
}
