package io.deeplay.grandmastery.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ColorTest {
  @Test
  void getOppositeFromWhite() {
    var color = Color.WHITE;
    Assertions.assertEquals(Color.BLACK, color.getOpposite());
  }

  @Test
  void getOppositeFromBlack() {
    var color = Color.BLACK;
    Assertions.assertEquals(Color.WHITE, color.getOpposite());
  }
}
