package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.ui.ConsoleUi;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/** Главный класс, который запускает локальную игру в шахматы. */
@Slf4j
public class LocalGame {
  private static final ConsoleUi consoleUi = new ConsoleUi(System.in, System.out);

  /**
   * Создает экземпляр GameController на основе выбранного режима игры и игроков.
   *
   * @return Экземпляр GameController для текущей игры.
   * @throws IOException Если возникает ошибка ввода-вывода при взаимодействии с консолью.
   */
  private static GameController createGameController() throws IOException {
    Player firstPlayer;
    Player secondPlayer;
    GameMode gameMode = consoleUi.selectMode();

    if (gameMode == GameMode.BOT_VS_BOT) {
      firstPlayer = new AiPlayer(Color.WHITE);
      secondPlayer = new AiPlayer(Color.BLACK);
    } else if (gameMode == GameMode.HUMAN_VS_BOT) {
      Color color = consoleUi.selectColor();
      if (color == Color.WHITE) {
        firstPlayer = new HumanPlayer(consoleUi.inputPlayerName(color), color, consoleUi);
        secondPlayer = new AiPlayer(Color.BLACK);
      } else {
        firstPlayer = new HumanPlayer(consoleUi.inputPlayerName(color), color, consoleUi);
        secondPlayer = new AiPlayer(Color.WHITE);
      }
    } else {
      firstPlayer = new HumanPlayer(consoleUi.inputPlayerName(Color.WHITE), Color.WHITE, consoleUi);
      secondPlayer =
          new HumanPlayer(consoleUi.inputPlayerName(Color.BLACK), Color.BLACK, consoleUi);
    }

    return new GameController(firstPlayer, secondPlayer);
  }

  /**
   * Локальная игра в шахматы.
   *
   * @param args Аргументы командной строки (не используются).
   */
  public static void main(String[] args) {
    try {
      GameController gameController = createGameController();
      gameController.beginPlay(consoleUi.selectChessType());
      consoleUi.printHelp();
      consoleUi.showBoard(gameController.getBoard(), Color.WHITE);
      while (!gameController.isGameOver()) {
        try {
          gameController.nextMove();
          consoleUi.showMove(gameController.getBoard(), gameController.getCurrentPlayer());
        } catch (GameException e) {
          consoleUi.incorrectMove();
        }
      }
      consoleUi.showResultGame(gameController.getGameStatus());
    } catch (GameException | IOException e) {
      log.error(e.getMessage());
    }
  }
}
