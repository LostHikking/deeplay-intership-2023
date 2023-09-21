package io.deeplay.grandmastery.motobots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.motostrategies.AttackingStrategy;
import io.deeplay.grandmastery.utils.Boards;
import org.junit.jupiter.api.Test;

class MiniMaxBotTest {

  @Test
  void moveNotNullTest() {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    GameHistory gameHistory = new GameHistory();
    Color mainColor = Color.WHITE;
    Color movingColor = Color.WHITE;
    State rootNode = new State(board, mainColor, movingColor, null, gameHistory, true);
    MiniMaxBot miniMaxBot = new MiniMaxBot("Moto", Color.WHITE, new AttackingStrategy(), 2);
    miniMaxBot.minimax(rootNode, 2, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
    assertNotNull(rootNode.getMove());
  }

  @Test
  void correctStateChoiceTest() {
    GameHistory gameHistory = new GameHistory();
    Board parentBoard = new HashBoard();
    parentBoard.setPiece(new Position(new Column(3), new Row(4)), new Knight(Color.WHITE));
    parentBoard.setPiece(new Position(new Column(0), new Row(0)), new King(Color.WHITE));
    parentBoard.setPiece(new Position(new Column(1), new Row(7)), new King(Color.BLACK));
    State parentState = new State(parentBoard, Color.BLACK, Color.BLACK, null, gameHistory, false);
    AttackingStrategy strategy = mock(AttackingStrategy.class);
    doNothing().when(strategy).setTerminalCost(any(State.class));
    doCallRealMethod().when(strategy).init(any());
    doCallRealMethod().when(strategy).setKingAttackCost(any());
    doCallRealMethod().when(strategy).evaluate(any());
    doCallRealMethod().when(strategy).setKingAttackCost(any());
    doCallRealMethod().when(strategy).setFinalStateCost();
    doCallRealMethod().when(strategy).setBasePiecesCost(any());
    MiniMaxBot bot = new MiniMaxBot("Moto", Color.BLACK, strategy, 1);
    int value = bot.minimax(parentState, 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
    int minValue = -30;
    assertEquals(value, minValue);
  }
}
