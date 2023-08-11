package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.dto.StartGameRequestDto;
import io.deeplay.grandmastery.dto.StartGameResponseDto;
import io.deeplay.grandmastery.ui.ConsoleUi;
import io.deeplay.grandmastery.utils.Boards;
import java.io.IOException;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
  private static final String HOST = "localhost";
  private static final int PORT = 8080;
  private final Dao dao;


  public Client(String host, int port) throws IOException {
    this.dao = new Dao(new Socket(host, port));
    log.info("Клиент успешно создан");
  }

  /**
   * Запускает клиент с определённым UI.
   *
   * @param ui UI
   * @throws IOException ошибка ввода/вывода
   */
  public void start(UI ui) throws IOException {
    var gameMode = ui.selectMode();
    var isBotVsBotGame = gameMode == GameMode.BOT_VS_BOT;
    var color = isBotVsBotGame ? Color.WHITE : ui.selectColor();
    var name = isBotVsBotGame ? "AI" : ui.inputPlayerName(color);
    var chessType = ui.selectChessType();

    var request = new StartGameRequestDto(name, gameMode, chessType, color);
    var response = dao.query(request, StartGameResponseDto.class);

    ui.printHelp();
    ui.showBoard(Boards.getBoardFromString(response.getBoard()), color);

    if (isBotVsBotGame) {
      return;
    }

    System.out.println("Continue...");
  }

  /**
   * Метод запускает клиент.
   *
   * @throws IOException Неудачная попытка чтения/записи
   */
  public static void main(String[] args) throws IOException {
    new Client(HOST, PORT).start(new ConsoleUi(System.in, System.out));
  }
}
