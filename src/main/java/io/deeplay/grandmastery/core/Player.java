package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.LongAlgebraicNotationParser;

/** Абстрактный класс, представлябщий игрока. */
public abstract class Player {
  /** Имя игрока. */
  private String name;
  /** Ход игрока в виде строки. */
  private Move moveData;
  /** Доска. */
  protected Board board;
  /** цвет игрока. */
  protected Color color;

  /**
   * Конструктор для плеера.
   * @param name Имя
   * @param board Доска
   * @param color Цвет
   */
  public Player(String name, Board board, Color color) {
    this.name = name;
    this.board = board;
    this.color = color;
  }

  /**
   * Метод, записывающий ход игрока.
   *
   * @param move Ход игрока
   */
  public void setMoveData(String move) {
    this.moveData = LongAlgebraicNotationParser.getMoveFromString(move);
  }

  public void setMoveData(Move move) {
    this.moveData = move;
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
