package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Randomus;
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
  protected static UI ui;

  protected static void createUi(String uiName) throws IllegalArgumentException {
    switch (uiName) {
      case "gui" -> ui = new Gui(true);
      case "tui" -> ui = new ConsoleUi(System.in, System.out);
      default -> throw new IllegalArgumentException("Неизвестный ui: " + uiName);
    }
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
    GameMode gameMode = ui.selectMode();

    if (gameMode == GameMode.BOT_VS_BOT) {
      firstPlayer = new Randomus(Color.WHITE);
      secondPlayer = new Randomus(Color.BLACK);
    } else if (gameMode == GameMode.HUMAN_VS_BOT) {
      Color color = ui.selectColor();
      firstPlayer = new HumanPlayer(ui.inputPlayerName(color), color, ui, false);
      secondPlayer = new Randomus(color.getOpposite());

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
      ui.showBoard(gameController.getBoard(), Color.WHITE);
      while (!gameController.isGameOver()) {
        try {
          gameController.nextMove();
          ui.showMove(
              gameController.getOpponentPlayer().getLastMove(),
              gameController.getOpponentPlayer().getColor());
          ui.showBoard(gameController.getBoard(), gameController.getCurrentPlayer().getColor());
        } catch (GameException e) {
          ui.incorrectMove();
        }
      }
      ui.showBoard(gameController.getBoard(), Color.WHITE);
      ui.showResultGame(gameController.getGameStatus());
    } catch (GameException | IOException e) {
      log.error(e.getMessage());
    } finally {
      ui.close();
    }
  }
}
