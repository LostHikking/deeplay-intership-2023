package io.deeplay.grandmastery.motobots;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.motostrategies.AttackingStrategy;
import io.deeplay.grandmastery.utils.Boards;
import org.junit.jupiter.api.Test;

class MiniMaxBotMctsTest {

  @Test
  void simulation() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    MiniMaxBotMcts expectiMaxBot =
        new MiniMaxBotMcts("Moto", Color.WHITE, new AttackingStrategy(), 2);
    int value = expectiMaxBot.simulation(board, Color.BLACK);
    assertTrue(value <= 1 && value >= -1);
  }

  @Test
  void moveNotNullTest() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    GameHistory gameHistory = new GameHistory();
    Color mainColor = Color.WHITE;
    Color movingColor = Color.WHITE;
    State rootNode = new State(board, mainColor, movingColor, null, gameHistory, true);
    MiniMaxBotMcts miniMaxBot =
        new MiniMaxBotMcts("Moto", Color.WHITE, new AttackingStrategy(), 2);
    miniMaxBot.minimaxMcts(rootNode, 2, true,Integer.MIN_VALUE, Integer.MAX_VALUE);
    assertNotNull(rootNode.getMove());
  }
}
