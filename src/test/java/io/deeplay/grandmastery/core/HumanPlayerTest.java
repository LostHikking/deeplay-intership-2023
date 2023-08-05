package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.LongAlgebraicNotationParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HumanPlayerTest {
  private Board board;
  private HumanPlayer player;
  private InputStream originalIn;
  private ByteArrayInputStream testIn;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void init() {
    player = new HumanPlayer("John Doe", board, Color.WHITE);
    originalIn = System.in;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void cleanup() {
    player.deleteLastMove();
    System.setIn(originalIn);
    System.setOut(System.out);
  }

  @Test
  void testMakeMoveValidMove() {
    String validMoveString = "a2a4";
    testIn = new ByteArrayInputStream(validMoveString.getBytes(StandardCharsets.UTF_8));
    System.setIn(testIn);
    Move validMove = LongAlgebraicNotationParser.getMoveFromString(validMoveString);
    player.makeMove();
    assertEquals(validMove, player.getMoveData());
  }

  @Test
  void testMakeMove_InvalidMove() {
    player = new HumanPlayer("John Doe", board, Color.WHITE);
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    String invalidMove = "a2m4";
    testIn = new ByteArrayInputStream(invalidMove.getBytes(StandardCharsets.UTF_8));
    System.setIn(testIn);
    player.makeMove();
    assertNull(player.getMoveData());
  }
}
