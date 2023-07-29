package io.deeplay.grandmastery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MainTest {
    @Test
    void main() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
