package io.deeplay.grandmastery;

import io.deeplay.grandmastery.algorithms.Algorithm;
import io.deeplay.grandmastery.algorithms.MiniMax;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import lombok.Getter;

@Getter
public class LjeDmitryBot extends Player {
  private final Algorithm algorithm;

  /**
   * Конструктор по умолчанию, с последней версией алгоритма.
   *
   * @param color Цвет
   */
  public LjeDmitryBot(Color color) {
    super("LjeDmitry", color);
    this.algorithm = new MiniMax(this, 3);
  }

  /**
   * Конструктор с настройками вручную.
   *
   * @param color Цвет
   * @param algorithmName название алгоритма
   * @param deep глубина построения дерева
   */
  public LjeDmitryBot(Color color, String algorithmName, int deep) {
    super("LjeDmitry", color);
    this.algorithm = getAlgorithm(algorithmName, deep);
  }

  private Algorithm getAlgorithm(String algorithmName, int deep) {
    return switch (algorithmName) {
      case "minimax" -> new MiniMax(this, deep);
      case "newMinimax" -> new NewMiniMax(this, deep);
      default -> throw new RuntimeException("Неизвестное название алгоритма: " + algorithmName);
    };
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
