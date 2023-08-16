package io.deeplay.grandmastery.listeners;

import java.io.IOException;

public interface InputListener {
  String inputMove(String playerName) throws IOException;

  boolean confirmSur() throws IOException;

  boolean answerDraw() throws IOException;
}
