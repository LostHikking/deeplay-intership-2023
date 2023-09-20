package io.deeplay.grandmastery;

import io.deeplay.grandmastery.algorithms.MiniMax;
import io.deeplay.grandmastery.algorithms.NegaMax;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.utils.Boards;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class LjeDmitryBotTest {
  @Test
  public void createDefaultBotTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE);
    bot.gameOver(GameState.DRAW);
    Assertions.assertAll(
        () -> Assertions.assertTrue(bot.getAlgorithm() instanceof NegaMax),
        () -> Assertions.assertTrue(((NegaMax) bot.getAlgorithm()).isShutdownPool()));
  }

  @Test
  public void createMiniMaxBotTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE, "minimax", 3);
    Assertions.assertTrue(bot.getAlgorithm() instanceof MiniMax);
  }

  @Test
  public void createNegaMaxBotTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE, "negamax", 3);
    bot.gameOver(GameState.DRAW);
    Assertions.assertTrue(bot.getAlgorithm() instanceof NegaMax);
  }

  @Test
  public void createUnknownBotTest() {
    Assertions.assertThrows(
        RuntimeException.class, () -> new LjeDmitryBot(Color.WHITE, "abababa", 3));
  }

  @Test
  @Timeout(5)
  public void createMoveTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE, "minimax", 3);
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);

    bot.startup(board);
    Assertions.assertNotNull(bot.createMove());
  }

  @Test
  @Timeout(value = 5250, unit = TimeUnit.MILLISECONDS)
  public void createMoveAfterTimeoutMinimaxTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE, "minimax", 10);
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);

    bot.startup(board);
    Assertions.assertNotNull(bot.createMove());
  }

  @Test
  @Timeout(value = 5250, unit = TimeUnit.MILLISECONDS)
  public void createMoveAfterTimeoutNegamaxTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE, new NegaMax(Color.WHITE, 10, 1));
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);

    bot.startup(board);
    Move move = bot.createMove();
    bot.gameOver(GameState.DRAW);
    Assertions.assertNotNull(move);
  }

  @Test
  public void answerDrawTest() {
    LjeDmitryBot bot = new LjeDmitryBot(Color.WHITE);
    Assertions.assertFalse(bot.answerDraw());
  }
}
