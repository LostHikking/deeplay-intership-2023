package io.deeplay.grandmastery.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HumanPlayerTest {
    private HumanPlayer player;
    private InputStream originalIn;
    private ByteArrayInputStream testIn;

    @BeforeEach
    void init() {
        player = new HumanPlayer("John Doe");
        // Сохраняем стандартный входной поток
        originalIn = System.in;
    }

    @AfterEach
    void cleanup() {
        // Восстанавливаем стандартный входной поток после каждого теста
        System.setIn(originalIn);
    }

    @Test
    void testMakeMove_ValidMove() {
        // Вводим корректный ход "a2a4"
        String validMove = "a2a4";
        testIn = new ByteArrayInputStream(validMove.getBytes(StandardCharsets.UTF_8));
        System.setIn(testIn);
        // Запускаем метод makeMove()
        player.makeMove();
        // Проверяем, что ход был установлен правильно
        assertEquals(validMove, player.getMoveData());
    }

    @Test
    void testMakeMove_InvalidMove() {
        String invalidMove = "a2m4";
        testIn = new ByteArrayInputStream(invalidMove.getBytes(StandardCharsets.UTF_8));
        System.setIn(testIn);
        assertThrows(IllegalArgumentException.class, () -> player.makeMove());
    }

}