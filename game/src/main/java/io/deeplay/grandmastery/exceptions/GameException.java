package io.deeplay.grandmastery.exceptions;

import io.deeplay.grandmastery.domain.GameErrorCode;

public class GameException extends RuntimeException {
  public GameException(GameErrorCode gameErrorCode) {
    super(gameErrorCode.getDescription());
  }

  @Override
  public void printStackTrace() {
    super.printStackTrace();
  }
}
