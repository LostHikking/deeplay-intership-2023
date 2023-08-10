package io.deeplay.grandmastery.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmptyUiTest {
  EmptyUi emptyUi;

  @BeforeEach
  void init() {
    emptyUi = new EmptyUi();
  }

  @Test
  void selectMode() {
    Assertions.assertNull(emptyUi.selectMode());
  }

  @Test
  void selectColor() {
    Assertions.assertNull(emptyUi.selectColor());
  }

  @Test
  void inputPlayerName() {
    Assertions.assertNull(emptyUi.inputPlayerName(null));
  }

  @Test
  void showMove() {
    Assertions.assertDoesNotThrow(() -> emptyUi.showMove(null, null));
  }

  @Test
  void showResultGame() {
    Assertions.assertDoesNotThrow(() -> emptyUi.showResultGame(null));
  }

  @Test
  void printHelp() {
    Assertions.assertDoesNotThrow(() -> emptyUi.printHelp());
  }

  @Test
  void inputMove() {
    Assertions.assertNull(emptyUi.inputMove(null));
  }

  @Test
  void showBoard() {
    Assertions.assertDoesNotThrow(() -> emptyUi.showBoard(null));
  }

  @Test
  void incorrectMove() {
    Assertions.assertDoesNotThrow(() -> emptyUi.incorrectMove());
  }

  @Test
  void emptyStartPosition() {
    Assertions.assertDoesNotThrow(() -> emptyUi.emptyStartPosition(null));
  }

  @Test
  void moveImpossible() {
    Assertions.assertDoesNotThrow(() -> emptyUi.moveImpossible(null));
  }

  @Test
  void warningYourKingInCheck() {
    Assertions.assertDoesNotThrow(() -> emptyUi.warningYourKingInCheck(null));
  }

  @Test
  void close() {
    Assertions.assertDoesNotThrow(() -> emptyUi.close());
  }
}
