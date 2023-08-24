package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.GameListener;

/** Абстрактный класс, представляющий игрока. */
public abstract class Player implements GameListener, PlayerInfo {
  protected final Game game;
  protected final String name;
  protected final Color color;

  protected Move lastMove;
  protected boolean gameOver;

  /**
   * Конструктор с параметрами.
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
    this.lastMove = move;
  }

  /**
   * Метод, который создает ход.
   *
   * @return {@code Move} ход игрока.
   * @throws GameException Если ход не валиден.
   */
  public abstract Move createMove() throws GameException;

  /**
   * Метод для принятия/отказа ничьи игроком.
   *
   * @return {@code true} если принял, {@code false} если отказался.
   * @throws GameException Если произошла ошибка ввода.
   */
  public abstract boolean answerDraw() throws GameException;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Move getLastMove() {
    return lastMove;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public void startup(Board board) throws GameException {
    game.startup(board);
    gameOver = false;
  }

  @Override
  public void makeMove(Move move) {
    game.makeMove(move);
  }

  @Override
  public void gameOver(GameState gameState) {
    gameOver = true;
    game.gameOver(gameState);
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public Board getBoard() {
    return game.getCopyBoard();
  }
}
