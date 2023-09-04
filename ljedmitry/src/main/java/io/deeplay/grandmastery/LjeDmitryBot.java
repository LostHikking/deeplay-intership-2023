package io.deeplay.grandmastery;

import io.deeplay.grandmastery.algorithms.Algorithm;
import io.deeplay.grandmastery.algorithms.MiniMax;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;

public class LjeDmitryBot extends Player {
  private final Algorithm algorithm = new MiniMax(this, 2);

  /**
   * Конструктор с параметрами.
   *
   * @param color Цвет
   */
  public LjeDmitryBot(Color color) {
    super("LjeDmitry", color);
  }

  @Override
  public Move createMove() throws GameException {
    Move move = algorithm.findBestMove(game.getCopyBoard(), gameHistory);
    lastMove = move;
    return move;
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }
}
