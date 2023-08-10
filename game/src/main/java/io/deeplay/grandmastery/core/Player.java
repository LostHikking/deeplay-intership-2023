package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;

/** Абстрактный класс, представляющий игрока. */
public abstract class Player implements PlayerListener, PlayerInfo {
  /** Имя игрока. */
  private final String name;
  /** Ход игрока в виде строки. */
  private Move moveData;
  /** Доска. */
  protected Board board;
  /** Цвет игрока. */
  protected Color color;

  protected UI ui;

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param board Доска
   * @param color Цвет
   */
  public Player(String name, Board board, Color color, UI ui) {
    this.name = name;
    this.board = board;
    this.color = color;
    this.ui = ui;
  }

  /**
   * Метод, записывающий ход игрока.
   *
   * @param move Ход игрока
   * @throws GameException Если ход не валиден.
   */
  public void setMoveData(String move) throws GameException {
    this.moveData = LongAlgebraicNotation.getMoveFromString(move);
  }

  public void setMoveData(Move move) {
    this.moveData = move;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Move getMoveData() {
    return moveData;
  }

  @Override
  public Color getColor() {
    return color;
  }

  public UI getUi() {
    return ui;
  }
}
