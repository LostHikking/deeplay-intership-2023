package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.deeplay.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PositionTest {

  @Test
  void getPositionFromStringTest() {
    var position1 = Position.getPositionFromString("a1");
    var position2 = Position.getPositionFromString("h8");
    var position3 = Position.getPositionFromString("e2");

    Assertions.assertAll(
        () -> assertEquals(0, position1.col().value()),
        () -> assertEquals(0, position1.row().value()),
        () -> assertEquals(7, position2.col().value()),
        () -> assertEquals(7, position2.row().value()),
        () -> assertEquals(4, position3.col().value()),
        () -> assertEquals(1, position3.row().value()));
  }

  @Test
  void getWrongPositionFromStringTest() {
    Assertions.assertAll(
        () -> assertThrows(GameException.class, () -> Position.getPositionFromString("a9")),
        () -> assertThrows(GameException.class, () -> Position.getPositionFromString("z1")),
        () -> assertThrows(GameException.class, () -> Position.getPositionFromString("z9")),
        () -> assertThrows(GameException.class, () -> Position.getPositionFromString("aa")),
        () -> assertThrows(GameException.class, () -> Position.getPositionFromString("22")));
  }

  @Test
  void positionToStringTest() {
    Position position = Position.getPositionFromString("e2");
    assertEquals("e2", Position.positionToString(position));
  }
}
