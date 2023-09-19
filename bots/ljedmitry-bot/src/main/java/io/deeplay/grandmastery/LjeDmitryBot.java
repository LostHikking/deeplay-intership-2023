package io.deeplay.grandmastery;

import io.deeplay.grandmastery.algorithms.Algorithm;
import io.deeplay.grandmastery.algorithms.MiniMax;
import io.deeplay.grandmastery.algorithms.NegaMax;
import io.deeplay.grandmastery.algorithms.ParallelAlgorithm;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import lombok.Getter;

@Getter
public class LjeDmitryBot extends Player {
  private static final long TIMEOUT_MILI = 4950;
  private final Algorithm algorithm;

  /**
   * Конструктор по умолчанию, с последней версией алгоритма.
   *
   * @param color Цвет
   */
  public LjeDmitryBot(Color color) {
    super("LjeDmitry", color);
    this.algorithm = new NegaMax(getColor(), 4);
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

  /**
   * Возвращает алгоритм по его названию и заданной глубине поиска.
   *
   * @param algorithmName Название алгоритма (например, "minimax" или "negamax").
   * @param deep Глубина поиска для алгоритма.
   * @return Экземпляр алгоритма соответствующего типа.
   * @throws RuntimeException Если передано неизвестное название алгоритма.
   */
  private Algorithm getAlgorithm(String algorithmName, int deep) {
    return switch (algorithmName) {
      case "minimax" -> new MiniMax(getColor(), deep);
      case "negamax" -> new NegaMax(getColor(), deep);
      default -> throw new RuntimeException("Неизвестное название алгоритма: " + algorithmName);
    };
  }

  @Override
  public Move createMove() throws GameException {
    Thread findBestMoveThread =
        new Thread(() -> lastMove = algorithm.findBestMove(game.getCopyBoard(), gameHistory));

    findBestMoveThread.start();
    try {
      findBestMoveThread.join(TIMEOUT_MILI);

      if (findBestMoveThread.isAlive()) {
        findBestMoveThread.interrupt();
        lastMove = algorithm.getBestMoveAfterTimout();
      }
    } catch (InterruptedException e) {
      throw GameErrorCode.ERROR_PLAYER_MAKE_MOVE.asException(e);
    }

    return lastMove;
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }

  @Override
  public void gameOver(GameState gameState) {
    super.gameOver(gameState);

    if (algorithm instanceof ParallelAlgorithm parallelAlgorithm) {
      parallelAlgorithm.shutdownPool();
    }
  }
}
