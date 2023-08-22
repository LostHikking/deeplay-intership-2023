package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.exceptions.GameException;
import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GrandmasteryApp {
  private static final Gui GUI = new Gui();

  private static GameController createGameController() throws IOException {
    Player firstPlayer;
    Player secondPlayer;
    GameMode gameMode = GUI.selectMode();

    if (gameMode == GameMode.BOT_VS_BOT) {
      firstPlayer = new AiPlayer(Color.WHITE);
      secondPlayer = new AiPlayer(Color.BLACK);
    } else if (gameMode == GameMode.HUMAN_VS_BOT) {
      Color color = GUI.selectColor();
      if (color == Color.WHITE) {
        firstPlayer = new HumanPlayer(GUI.inputPlayerName(color), color, GUI);
        secondPlayer = new AiPlayer(Color.BLACK);
      } else {
        firstPlayer = new HumanPlayer(GUI.inputPlayerName(color), color, GUI);
        secondPlayer = new AiPlayer(Color.WHITE);
      }
    } else {
      firstPlayer = new HumanPlayer(GUI.inputPlayerName(Color.WHITE), Color.WHITE, GUI);
      secondPlayer = new HumanPlayer(GUI.inputPlayerName(Color.BLACK), Color.BLACK, GUI);
    }

    return new GameController(firstPlayer, secondPlayer);
  }

  /**
   * Это основной метод программы, который отвечает за запуск игры.
   * Показывается GUI, и доска отображается с начальным положением фигур.
   * SwingWorker используется для выполнения игровой логики в фоновом потоке, обновляя
   * GUI в основном потоке.(чтобы не было фризов).
   *
   * @param args аргументы
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          try {
            GameController gameController = createGameController();
            gameController.beginPlay(GUI.selectChessType());
            GUI.showGui();
            GUI.showBoard(gameController.getBoard(), Color.WHITE);

            SwingWorker<Void, Void> worker =
                new SwingWorker<Void, Void>() {
                  @Override
                  protected Void doInBackground() throws Exception {
                    while (!gameController.isGameOver()) {
                      try {
                        GUI.setMovingPlayer(gameController.getCurrentPlayer().getName());
                        gameController.nextMove();
                        publish(); // Обновляем интерфейс в основном потоке
                      } catch (GameException e) {
                        GUI.incorrectMove();
                      }
                    }
                    return null;
                  }

                  @Override
                  protected void process(List<Void> chunks) {
                    GUI.showBoard(
                        gameController.getBoard(), gameController.getCurrentPlayer().getColor());
                    GUI.showMove(gameController.getOpponentPlayer());
                  }

                  @Override
                  protected void done() {
                    try {
                      GUI.showResultGame(gameController.getGameStatus());
                    } catch (Exception e) {
                      log.error(e.getMessage());
                    }
                  }
                };

            worker.execute(); // Запускаем фоновый поток

          } catch (GameException | IOException e) {
            log.error(e.getMessage());
          }
        });
  }
}
