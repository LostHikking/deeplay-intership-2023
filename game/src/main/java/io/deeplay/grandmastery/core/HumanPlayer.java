package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import java.io.IOException;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  /** Пользовательский интерфейс. */
  private final UI userInterface;

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param board Доска
   * @param color Цвет
   */
  public HumanPlayer(String name, Board board, Color color, UI ui) {
    super(name, board, color);
    this.userInterface = ui;
  }

  /** Метод, отвечающий за ввод хода игрока. */
  @Override
  public void makeMove() throws GameException {
    try {
      setMoveData(userInterface.inputMove(this.getName()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
