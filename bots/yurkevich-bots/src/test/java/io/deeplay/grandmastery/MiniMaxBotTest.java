package io.deeplay.grandmastery;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class MiniMaxBotTest {

  @Test
  public void testCreateChildNodes() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    GameHistory gameHistory = new GameHistory();
    Color mainColor = Color.WHITE;
    Color movingColor = Color.WHITE;
    StateNode rootNode = new StateNode(board, mainColor, movingColor, null, gameHistory, true);
    MiniMaxBot miniMaxBot = new MiniMaxBot("Moto", Color.WHITE, 2);
    ArrayList<StateNode> childNodes = miniMaxBot.createChildNodes(rootNode);

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

  @Test
  void minimax() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    GameHistory gameHistory = new GameHistory();
    Color mainColor = Color.WHITE;
    Color movingColor = Color.WHITE;
    StateNode rootNode = new StateNode(board, mainColor, movingColor, null, gameHistory, true);
    MiniMaxBot miniMaxBot = new MiniMaxBot("Moto", Color.WHITE, 2);
    miniMaxBot.minimax(rootNode, 2, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
    assertNotNull(rootNode.getMove());
  }
}
