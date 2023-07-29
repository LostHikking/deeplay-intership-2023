package com.grandmastery.exceptions;

import com.grandmastery.domain.GameErrorCode;

public class GameException extends RuntimeException {
    public GameException(GameErrorCode gameErrorCode) {
        super(gameErrorCode.getDescription());
    }
}
