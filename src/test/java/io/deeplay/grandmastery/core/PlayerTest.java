package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class PlayerTest {
  private Player player;

  @BeforeEach
  void init() {
    player = new HumanPlayer("John Doe");
  }

  @Test
  void testSetValidMoveData() {

    String validMove = "a2a4";
    assertDoesNotThrow(() -> player.setMoveData(validMove));
    assertEquals(validMove, player.getMoveData());
  }

  @Test
  void testSetInvalidMoveData() {

    String invalidMove = "a2a4e";
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> player.setMoveData(invalidMove));
  }
}
