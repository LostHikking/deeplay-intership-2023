package io.deeplay.grandmastery.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    @BeforeEach
    void init(){player = new HumanPlayer("John Doe");}

    @Test
    void testSetValidMoveData() {

        String validMove = "a2a4";
        assertDoesNotThrow(() -> player.setMoveData(validMove));
        assertEquals(validMove, player.getMoveData());
    }

    @Test
    void testSetInvalidMoveData_Format() {

        String invalidMove = "a2a4e"; // Некорректный формат хода
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.setMoveData(invalidMove));
        assertEquals("Некорректный формат хода. Используйте формат 'a2a4'!", exception.getMessage());
    }


}