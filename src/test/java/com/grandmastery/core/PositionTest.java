package com.grandmastery.core;

import com.grandmastery.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void getPositionFromString() {
        var position1 = Position.getPositionFromString("a1");
        var position2 = Position.getPositionFromString("h8");
        var position3 = Position.getPositionFromString("e2");

        Assertions.assertAll(
                () -> assertEquals(1, position1.col()),
                () -> assertEquals(1, position1.row()),
                () -> assertEquals(8, position2.col()),
                () -> assertEquals(8, position2.row()),
                () -> assertEquals(5, position3.col()),
                () -> assertEquals(2, position3.row())
        );
    }

    @Test
    void getWrongPositionFromString() {
        Assertions.assertAll(
                () -> assertThrows(GameException.class,
                        () -> Position.getPositionFromString("a9")),
                () -> assertThrows(GameException.class,
                        () -> Position.getPositionFromString("z1")),
                () -> assertThrows(GameException.class,
                        () -> Position.getPositionFromString("z9")),
                () -> assertThrows(GameException.class,
                        () -> Position.getPositionFromString("aa")),
                () -> assertThrows(GameException.class,
                        () -> Position.getPositionFromString("22"))
        );
    }
}
