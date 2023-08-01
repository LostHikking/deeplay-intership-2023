package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




public class HumanPlayerTest {

  private HumanPlayer player;
  private InputStream originalIn;
  private ByteArrayInputStream testIn;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void init() {
    player = new HumanPlayer("John Doe");
    originalIn = System.in;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void cleanup() {
    System.setIn(originalIn);
    System.setOut(System.out);
  }

  @Test
  void testMakeMoveValidMove() {
    String validMove = "a2a4";
    testIn = new ByteArrayInputStream(validMove.getBytes(StandardCharsets.UTF_8));
    System.setIn(testIn);
    player.makeMove();
    assertEquals(validMove, player.getMoveData());
  }

  /*@Test
  void testMakeMoveInvalidMove(){
      String invalidMove = "a2m4";
      testIn = new ByteArrayInputStream(invalidMove.getBytes(StandardCharsets.UTF_8));
      System.setIn(testIn);
      player.makeMove();
      String expectedErrorMessage = "Некорректный ход! Пожалуйста, введите ход правильно.";
      String consoleOutput = outContent.toString(StandardCharsets.UTF_8).trim();
      assertTrue(consoleOutput.contains(expectedErrorMessage));
  }*/
}
