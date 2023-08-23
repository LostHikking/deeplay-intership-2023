package io.deeplay.grandmastery.exceptions;

import io.deeplay.grandmastery.domain.GameErrorCode;

public class GameException extends RuntimeException {
  public GameException(GameErrorCode gameErrorCode) {
    super(gameErrorCode.getDescription());
  }

  public GameException(GameErrorCode gameErrorCode, Exception e) {
    super(gameErrorCode.getDescription(), e);
  }
}
