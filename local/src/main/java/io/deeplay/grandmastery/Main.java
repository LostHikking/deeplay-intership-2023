package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.ui.ConsoleUi;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    try {
      GameController gameController = new GameController();
      gameController.setUi(new ConsoleUi(System.in, System.out));
      gameController.beginPlay();
      while (!gameController.isGameOver()) {
        gameController.nextMove();
      }
    } catch (GameException | IOException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
