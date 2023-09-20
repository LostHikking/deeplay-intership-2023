package io.deeplay.grandmastery.motobots;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.motostrategies.AttackingStrategy;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class BotTest {

  @Test
  public void testCreateChildNodes() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    GameHistory gameHistory = new GameHistory();
    Color mainColor = Color.WHITE;
    Color movingColor = Color.WHITE;
    State rootNode = new State(board, mainColor, movingColor, null, gameHistory, true);
    MiniMaxBot miniMaxBot = new MiniMaxBot("Moto", Color.WHITE, new AttackingStrategy(), 2);
    ArrayList<State> childNodes = miniMaxBot.createChildStates(rootNode);

    int expectedPawnMoves =
        16; // В начальной позиции у каждого игрока 8 пешек, имеющих по 2 возможных хода
    int expectedKnightMoves =
        4; // В начальной позиции у каждого игрока 2 коня, каждый имеет 2 возможных хода
    int expectedTotalMoves = expectedPawnMoves + expectedKnightMoves;
    assertEquals(expectedTotalMoves, childNodes.size());
    if (rootNode.isMainNode()) {
      childNodes.forEach(
          childNode -> assertEquals(rootNode.getMainColor(), childNode.getMovingColor()));
    } else {
      childNodes.forEach(
          childNode -> assertEquals(rootNode.getOpponentColor(), childNode.getMovingColor()));
    }
  }
}
