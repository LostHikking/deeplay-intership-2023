package io.deeplay.core;

import io.deeplay.exceptions.GameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void getPositionFromString() {
        var position = Position.getPositionFromString("a1");
        assertEquals(1, position.col());
        assertEquals(1, position.row());

        position = Position.getPositionFromString("h8");
        assertEquals(8, position.col());
        assertEquals(8, position.row());

        position = Position.getPositionFromString("e2");
        assertEquals(5, position.col());
        assertEquals(2, position.row());
    }

    @Test
    void getWrongPositionFromString() {
        assertThrows(GameException.class, () -> Position.getPositionFromString("a9"));
        assertThrows(GameException.class, () -> Position.getPositionFromString("z1"));
        assertThrows(GameException.class, () -> Position.getPositionFromString("z9"));
        assertThrows(GameException.class, () -> Position.getPositionFromString("aa"));
        assertThrows(GameException.class, () -> Position.getPositionFromString("22"));
    }
}