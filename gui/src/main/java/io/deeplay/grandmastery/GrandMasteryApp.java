package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.*;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.exceptions.GameException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

import static io.deeplay.grandmastery.utils.Boards.defaultChess;

@Slf4j
public class GrandMasteryApp {
  private static final GameGui gameGui = new GameGui();

  private static GameController createGameController() throws IOException {
    Player firstPlayer;
    Player secondPlayer;
    GameMode gameMode = gameGui.selectMode();

    if (gameMode == GameMode.BOT_VS_BOT) {
      firstPlayer = new AiPlayer(Color.WHITE);
      secondPlayer = new AiPlayer(Color.BLACK);
    } else if (gameMode == GameMode.HUMAN_VS_BOT) {
      Color color = gameGui.selectColor();
      if (color == Color.WHITE) {
        firstPlayer = new HumanPlayer(gameGui.inputPlayerName(color), color, gameGui);
        secondPlayer = new AiPlayer(Color.BLACK);
      } else {
        firstPlayer = new HumanPlayer(gameGui.inputPlayerName(color), color, gameGui);
        secondPlayer = new AiPlayer(Color.WHITE);
      }
    } else {
      firstPlayer = new HumanPlayer(gameGui.inputPlayerName(Color.WHITE), Color.WHITE, gameGui);
      secondPlayer = new HumanPlayer(gameGui.inputPlayerName(Color.BLACK), Color.BLACK, gameGui);
    }

    return new GameController(firstPlayer, secondPlayer);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          try {
            GameController gameController = createGameController();
            gameController.beginPlay(gameGui.selectChessType());
            gameGui.showGui();
            gameGui.showBoard(gameController.getBoard(), Color.WHITE);

            SwingWorker<Void, Void> worker =
                new SwingWorker<Void, Void>() {
                  @Override
                  protected Void doInBackground() throws Exception {
                    while (!gameController.isGameOver()) {
                      try {
                        gameController.nextMove();
                        publish(); // Обновляем интерфейс в основном потоке
                      } catch (GameException e) {
                        gameGui.incorrectMove();
                      }
                    }
                    return null;
                  }

                  @Override
                  protected void process(List<Void> chunks) {
                    gameGui.showMove(gameController.getBoard(), gameController.getCurrentPlayer());
                  }

                  @Override
                  protected void done() {
                    try {
                      gameGui.showResultGame(gameController.getGameStatus());
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
