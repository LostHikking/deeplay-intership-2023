package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.GetListBotsFromFarm;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.dto.SendListBots;
import io.deeplay.grandmastery.exceptions.QueryException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientController {
  private static final int TIME_RECONNECTION = 5000;

  private final UI ui;
  private ClientDao clientDao;

  public ClientController(UI ui) {
    this.ui = ui;
  }

  /**
   * Метод для подключения клиента к серверу. Если подключение не возможно, будет повторная попытка
   * через {@code TIME_RECONNECTION} мс. И так до тех пор, пока не будет успешного подключения.
   */
  protected void connect(String host, int port) throws InterruptedException {
    while (true) {
      try {
        Socket socket = new Socket(host, port);
        BufferedReader in =
            new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        BufferedWriter out =
            new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

        clientDao = new ClientDao(socket, in, out);
        log.info("Соединение с сервером установлено.");
        printEventMessage("Соединение с сервером установлено.");
        break;
      } catch (IOException e) {
        log.warn("Сервер недоступен. Попробуем снова через некоторое время...");
        printEventMessage("Сервер недоступен. Попробуем снова через некоторое время...");
        waitAndReconnect();
      }
    }
  }

  public void printEventMessage(String event) {
    ui.printEventMessage(event);
  }

  private void waitAndReconnect() throws InterruptedException {
    Thread.sleep(TIME_RECONNECTION);
  }

  public GameMode selectMode() throws IOException {
    return ui.selectMode();
  }

  public Color selectColor() throws IOException {
    return ui.selectColor();
  }

  public String inputPlayerName(Color color) throws IOException {
    return ui.inputPlayerName(color);
  }

  public ChessType selectChessType() throws IOException {
    return ui.selectChessType();
  }

  public IDto query(IDto request) throws QueryException {
    return clientDao.query(request);
  }

  public void showBotVsBotMove(Board board, Color color) {
    ui.showBoard(board, Color.WHITE);
    ui.showMove(board.getLastMove(), color);
  }

  public void showBoard(Board board, Color color) {
    ui.showBoard(board, color);
  }

  public void showResultGame(GameState gameState) {
    ui.showResultGame(gameState);
  }

  public String getJsonFromServer() throws IOException {
    return clientDao.getJsonFromServer();
  }

  public void close() throws IOException {
    clientDao.close();
  }

  public synchronized void send(String json) throws IOException {
    clientDao.send(json);
  }

  public boolean isClosed() {
    return clientDao.isClosed();
  }

  public String selectBot(Color color) throws IOException {
    List<String> bots = ((SendListBots) clientDao.query(new GetListBotsFromFarm())).getBots();
    return ui.selectBot(bots, color);
  }

  public UI getUi() {
    return ui;
  }
}
