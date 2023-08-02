package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.utils.LongAlgebraicNotationParser;

/** Абстрактный класс, представлябщий игрока. */
public abstract class Player {
  /** Имя игрока. */
  private String name;
  /** Ход игрока в виде строки. */
  private Move moveData;

  public Player(String name) {
    this.name = name;
  }

  /**
   * Метод, записывающий ход игрока.
   *
   * @param move Ход игрока
   */
  public void setMoveData(String move) {
    this.moveData = LongAlgebraicNotationParser.getMoveFromString(move);
  }

  public void deleteLastMove() {
    moveData = null;
  }

  public String getName() {
    return name;
  }

  public Move getMoveData() {
    return moveData;
  }

  public abstract boolean makeMove();
}
