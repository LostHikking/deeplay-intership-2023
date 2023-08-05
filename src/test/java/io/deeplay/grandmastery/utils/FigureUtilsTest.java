package io.deeplay.grandmastery.utils;

import static io.deeplay.grandmastery.utils.FigureUtils.hasFigureOnDiagonalBetweenPositions;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Bishop;
import org.junit.jupiter.api.Test;

class FigureUtilsTest {
  private Board board;

  @Test
  void hasFigureOnDiagonalBetweenPositionsTest() {
    board = new HashBoard();
    board.setPiece(new Position(new Column(3), new Row(3)), new Bishop(Color.WHITE));
    assertTrue(hasFigureOnDiagonalBetweenPositions(board, 0, 7, 0, 7));
  }
}
