package io.deeplay.grandmastery.core;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UiTest {
  @Test
  public void getUiFromConfigTest() throws IOException {
    Assertions.assertEquals("gui", UI.getUiFromConfig());
  }
}
