package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.ui.ConsoleUi;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/** Главный класс, который запускает локальную игру в шахматы. */
@Slf4j
public class Grandmastery {
  private static Color defaultColor = Color.WHITE;
  private static GameMode gameMode;
  protected static UI ui;

  protected static void createUi(String uiName) throws IllegalArgumentException {
    switch (uiName) {
      case "gui" -> ui = new Gui(true);
      case "tui" -> ui = new ConsoleUi(System.in, System.out);
      default -> throw new IllegalArgumentException("Неизвестный ui: " + uiName);
    }
  }

  protected static Player createBot(Color color) throws IllegalArgumentException, IOException {
    return BotFactory.create(ui.selectBot(Bots.getBotsList(), color), color);
  }

  /**
   * Создает экземпляр GameController на основе выбранного режима игры и игроков.
   *
   * @return Экземпляр GameController для текущей игры.
   * @throws IOException Если возникает ошибка ввода-вывода при взаимодействии с консолью.
   */
  protected static GameController createGameController() throws IOException {
    Player firstPlayer;
    Player secondPlayer;
    gameMode = ui.selectMode();

    if (gameMode == GameMode.BOT_VS_BOT) {
      firstPlayer = createBot(Color.WHITE);
      secondPlayer = createBot(Color.BLACK);
    } else if (gameMode == GameMode.HUMAN_VS_BOT) {
      defaultColor = ui.selectColor();
      firstPlayer = new HumanPlayer(ui.inputPlayerName(defaultColor), defaultColor, ui, false);
      secondPlayer = createBot(defaultColor.getOpposite());
    } else {
      firstPlayer = new HumanPlayer(ui.inputPlayerName(Color.WHITE), Color.WHITE, ui, false);
      secondPlayer = new HumanPlayer(ui.inputPlayerName(Color.BLACK), Color.BLACK, ui, false);
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
      createUi(UI.getUiFromConfig());
      GameController gameController = createGameController();
      gameController.beginPlay(ui.selectChessType());

      ui.printHelp();
      ui.showBoard(gameController.getBoard(), defaultColor);

      while (!gameController.isGameOver()) {
        try {
          gameController.nextMove();
          ui.showMove(
              gameController.getOpponentPlayer().getLastMove(),
              gameController.getOpponentPlayer().getColor());

          Color displayColor =
              gameMode == GameMode.HUMAN_VS_HUMAN
                  ? gameController.getCurrentPlayer().getColor()
                  : defaultColor;
          ui.showBoard(gameController.getBoard(), displayColor);
        } catch (GameException e) {
          ui.incorrectMove();
        }
      }

      ui.showBoard(gameController.getBoard(), defaultColor);
      ui.showResultGame(gameController.getGameStatus());
    } catch (GameException | IOException e) {
      log.error(e.getMessage());
    } finally {
      ui.close();
    }
  }
}
