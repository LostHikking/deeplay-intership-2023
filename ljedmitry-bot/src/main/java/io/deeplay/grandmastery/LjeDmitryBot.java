package io.deeplay.grandmastery;

import io.deeplay.grandmastery.algorithms.Algorithm;
import io.deeplay.grandmastery.algorithms.MiniMax;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LjeDmitryBot extends Player {
  private final Algorithm algorithm;
  private final int deep;

  /**
   * Конструктор с настройками из конфига.
   *
   * @param color Цвет
   * @throws RuntimeException При ошибке чтения конфига.
   */
  public LjeDmitryBot(Color color) {
    super("LjeDmitry", color);

    try (InputStream config =
        LjeDmitryBot.class.getClassLoader().getResourceAsStream("/config.properties")) {
      Properties properties = new Properties();
      properties.load(config);

      deep = Integer.parseInt(properties.getProperty("deep"));
      algorithm = getAlgorithm(properties.getProperty("algorithm"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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

    this.deep = deep;
    this.algorithm = getAlgorithm(algorithmName);
  }

  private Algorithm getAlgorithm(String algorithmName) {
    if (algorithmName.equals("minimax")) {
      return new MiniMax(this, deep);
    }
    throw new RuntimeException("Неизвестное название алгоритма для бота!");
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
