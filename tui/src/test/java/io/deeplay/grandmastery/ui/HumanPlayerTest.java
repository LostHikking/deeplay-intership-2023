package io.deeplay.grandmastery.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

public class HumanPlayerTest {
  private final Board board = new HashBoard();
  private final Color color = Color.WHITE;
  private final String playerName = "TestPlayer";
  private ConsoleUi consoleUi;

  @Test
  public void makeMoveValidInputTest() throws GameException, IOException {
    String testInput = "e2e4\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    System.setIn(inputStream);
    consoleUi = new ConsoleUi(inputStream, OutputStream.nullOutputStream());

    HumanPlayer humanPlayer = new HumanPlayer(playerName, board, color, consoleUi);
    humanPlayer.makeMove();

    String expectedMoveData = "e2e4";
    assertEquals(expectedMoveData, LongAlgebraicNotation.moveToString(humanPlayer.getMoveData()));
  }

  @Test
  public void makeMoveWithInvalidInputTest() throws IOException {
    String testInput = "e2k4\n";
    InputStream inputStream =
        new ByteArrayInputStream(testInput.getBytes(Charset.defaultCharset()));
    System.setIn(inputStream);
    consoleUi = new ConsoleUi(inputStream, OutputStream.nullOutputStream());

    Player humanPlayer = new HumanPlayer(playerName, board, color, consoleUi);
    assertThrows(GameException.class, humanPlayer::makeMove);
  }
}
