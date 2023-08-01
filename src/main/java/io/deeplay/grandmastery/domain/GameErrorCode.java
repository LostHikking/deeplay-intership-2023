package io.deeplay.grandmastery.domain;

import io.deeplay.grandmastery.exceptions.GameException;

public enum GameErrorCode {
  MOVE_NOT_FOUND("Ход не найден"),
  INCORRECT_MOVE_FORMAT("Неверный формат записи хода"),
  INCORRECT_FIGURE_CHARACTER("Неверный символ обозначения фигуры"),
  INCORRECT_POSITION_FORMAT("Неверный формат позиции"),
  NULL_POINTER_SOURCE_BOARD("Параметр sourceBoard не может быть равен null");

  private final String description;

  GameErrorCode(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public GameException asException() {
    return new GameException(this);
  }
}
