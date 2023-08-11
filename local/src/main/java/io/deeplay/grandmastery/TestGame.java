package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestGame {
  /**
   * Локальная игра в шахматы.
   *
   * @param args Аргументы командной строки (не используются).
   */
  public static void main(String[] args) {
    var countTest = 1000;
    var errorTest = 0;
    var incorrectMove = 0;

    for (int i = 0; i < countTest; i++) {
      log.info("Игра номер " + i);
      try {
        var player1 = new AiPlayer(Color.WHITE);
        var player2 = new AiPlayer(Color.BLACK);
        var gameController = new GameController(player1, player2);

        gameController.beginPlay(ChessType.CLASSIC);

        while (!gameController.isGameOver()) {
          try {
            gameController.nextMove();
          } catch (GameException e) {
            incorrectMove++;
          }
        }
      } catch (Exception e) {
        errorTest++;
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }

    log.info("Всего тестов: " + countTest);
    log.info("Закончились с ошибкой: " + errorTest);
    log.info("Ошибок некорректного хода: " + incorrectMove);
  }
}
