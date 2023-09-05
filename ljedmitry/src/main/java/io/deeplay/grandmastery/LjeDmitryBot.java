package io.deeplay.grandmastery;

import com.sun.tools.javac.Main;
import io.deeplay.grandmastery.algorithms.Algorithm;
import io.deeplay.grandmastery.algorithms.MiniMax;
import io.deeplay.grandmastery.algorithms.NegaMax;
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
   * Конструктор с параметрами.
   *
   * @param color Цвет
   */
  public LjeDmitryBot(Color color) {
    super("LjeDmitry", color);

    try (InputStream config =
        Main.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties properties = new Properties();
      properties.load(config);

      deep = Integer.parseInt(properties.getProperty("deep"));
      algorithm = getAlgorithm(properties.getProperty("algorithm"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Algorithm getAlgorithm(String algorithmName) {
    switch (algorithmName) {
      case "minmax" -> {
        return new MiniMax(this, deep);
      }
      case "negamax" -> {
        return new NegaMax(this, deep);
      }
      default -> throw new RuntimeException("Неизвестное название алгоритма для бота!");
    }
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
