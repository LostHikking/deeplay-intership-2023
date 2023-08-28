package io.deeplay.grandmastery;

import io.deeplay.grandmastery.bots.Randomus;
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
      firstPlayer = new Randomus(Color.WHITE);
      secondPlayer = new Randomus(Color.BLACK);
    } else if (gameMode == GameMode.HUMAN_VS_BOT) {
      Color color = consoleUi.selectColor();
      firstPlayer = new HumanPlayer(consoleUi.inputPlayerName(color), color, consoleUi, false);

      if (color == Color.WHITE) {
        secondPlayer = new Randomus(Color.BLACK);
      } else {
        secondPlayer = new Randomus(Color.WHITE);
      }
    } else {
      firstPlayer =
          new HumanPlayer(consoleUi.inputPlayerName(Color.WHITE), Color.WHITE, consoleUi, false);
      secondPlayer =
          new HumanPlayer(consoleUi.inputPlayerName(Color.BLACK), Color.BLACK, consoleUi, false);
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
          consoleUi.showMove(
              gameController.getOpponentPlayer().getLastMove(),
              gameController.getOpponentPlayer().getColor());
          consoleUi.showBoard(
              gameController.getBoard(), gameController.getCurrentPlayer().getColor());
        } catch (GameException e) {
          consoleUi.incorrectMove();
        }
      }
      consoleUi.showBoard(gameController.getBoard(), Color.WHITE);
      consoleUi.showResultGame(gameController.getGameStatus());
    } catch (GameException | IOException e) {
      log.error(e.getMessage());
    }
  }
}
