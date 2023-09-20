package io.deeplay.grandmastery.checks;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.params.provider.Arguments;

public class PuzzleDataProvider {
  public static <T extends Color, U extends Board, V extends List<Move>, W extends Queue<Move>>
      Arguments createArguments(T color, U board, V expectMoves, W enemyMoves, String puzzleName) {
    return Arguments.of(color, board, expectMoves, enemyMoves, puzzleName);
  }
}
