package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;

/** Абстрактный класс, представляющий игрока. */
public abstract class Player implements GameListener, PlayerInfo {
  /** Имя игрока. */
  private final String name;
  /** Ход игрока в виде строки. */
  private String lastMove;

  protected Game game;
  /** Цвет игрока. */
  protected Color color;

  protected boolean gameOver;

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param color Цвет
   */
  public Player(String name, Color color) {
    this.name = name;
    this.color = color;
    this.game = new Game();
  }

  /**
   * Метод, записывающий ход игрока.
   *
   * @param move Ход игрока
   * @throws GameException Если ход не валиден.
   */
  public void setLastMove(Move move) throws GameException {
    this.lastMove = LongAlgebraicNotation.moveToString(move);
  }

  public void setLastMove(String move) throws GameException {
    this.lastMove = move;
  }

  public abstract Move createMove() throws GameException;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getLastMove() {
    return lastMove;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public void startup(Board board) {
    game.startup(board);
    gameOver = false;
  }

  @Override
  public void makeMove(Move move) {
    game.makeMove(move);
  }

  @Override
  public void gameOver() {
    gameOver = true;
  }
}
