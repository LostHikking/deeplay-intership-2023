package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.InputListener;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.IOException;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  private final InputListener inputListener;

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param color Цвет
   */
  public HumanPlayer(String name, Color color, InputListener inputListener) {
    super(name, color);
    this.inputListener = inputListener;
  }

  private String notifyInputListener() throws IOException {
    return inputListener.inputMove(this.getName());
  }

  /**
   * Метод, отвечающий за ввод хода игрока.
   *
   * @return {@code Move} ход игрока.
   */
  @Override
  public Move createMove() throws GameException {
    if (this.isGameOver()) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }
    try {
      Move move = LongAlgebraicNotation.getMoveFromString(notifyInputListener());
      setLastMove(move);
      return move;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
