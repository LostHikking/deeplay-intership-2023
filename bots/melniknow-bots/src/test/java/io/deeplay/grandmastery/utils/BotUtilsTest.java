package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BotUtilsTest {
  @Test
  void getPossibleMovesFromStartPositionForWhite() {
    var board = new HashBoard();
    Boards.defaultChess().accept(board);

    var possibleMoves = BotUtils.getPossibleMoves(board, Color.WHITE);
    Assertions.assertEquals(20, possibleMoves.size());
  }

  @Test
  void getPossibleMovesFromStartPositionForBlack() {
    var board = new HashBoard();
    Boards.defaultChess().accept(board);

    var possibleMoves = BotUtils.getPossibleMoves(board, Color.BLACK);
    Assertions.assertEquals(20, possibleMoves.size());
  }

  @Test
  void getCopyBoardAfterMove() {
    var startBoard = new HashBoard();
    Boards.defaultChess().accept(startBoard);

    var board = new HashBoard();
    Boards.defaultChess().accept(board);

    var newBoard =
        BotUtils.getCopyBoardAfterMove(LongAlgebraicNotation.getMoveFromString("e2e4"), board);

    Assertions.assertAll(
        () -> Assertions.assertTrue(Boards.equals(startBoard, board)),
        () -> Assertions.assertFalse(Boards.equals(startBoard, newBoard)));
  }
}
