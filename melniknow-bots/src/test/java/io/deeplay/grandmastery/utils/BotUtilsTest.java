package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BotUtilsTest {

  @Test
  void getOtherColorFromWhite() {
    var color = Color.WHITE;
    Assertions.assertEquals(Color.BLACK, BotUtils.getOtherColor(color));
  }

  @Test
  void getOtherColorFromBlack() {
    var color = Color.BLACK;
    Assertions.assertEquals(Color.WHITE, BotUtils.getOtherColor(color));
  }

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

  @Test
  void getFenFromBoard() {
    var board = new HashBoard();
    Boards.defaultChess().accept(board);

    var fen = BotUtils.getFenFromBoard(board, Color.WHITE, new GameHistory());
    var excepted = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Assertions.assertEquals(excepted, fen);
  }

  @Test
  void writeFenBoard() {
    var stringBuilder = new StringBuilder();
    var board = new HashBoard();

    Boards.defaultChess().accept(board);
    BotUtils.writeFenBoard(stringBuilder, board);

    var excepted = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    Assertions.assertEquals(excepted, stringBuilder.toString());
  }
}
