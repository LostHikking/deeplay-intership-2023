package io.deeplay.exceptions;

import io.deeplay.domain.GameErrorCode;

public class GameException extends RuntimeException {
    public GameException(GameErrorCode gameErrorCode) {
        super(gameErrorCode.getDescription());
    }
}
