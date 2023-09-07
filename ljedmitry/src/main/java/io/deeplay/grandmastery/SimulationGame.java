package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulationGame {
  private static final boolean WITH_GUI = true;
  private static Gui gui;

  public static void main(String[] args) {
    if (WITH_GUI) {
      gui = new Gui(true);
    }

    Board board = new HashBoard();
    board.setPiece(Position.fromString("e5"), new King(Color.BLACK));
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("c1"), new Bishop(Color.WHITE));
    board.setPiece(Position.fromString("f1"), new Bishop(Color.WHITE));

    try {
      GameController gameController =
          new GameController(new LjeDmitryBot(Color.WHITE), new LjeDmitryBot(Color.BLACK));
      gameController.beginPlay(board);
      if (WITH_GUI) {
        gui.showBoard(gameController.getBoard(), Color.WHITE);
      }

      while (!gameController.isGameOver()) {
        gameController.nextMove();

        if (WITH_GUI) {
          gui.showBoard(gameController.getBoard(), Color.WHITE);
        }
      }

      GameState status = gameController.getGameStatus();
      log.info("Complete, result: " + status);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
