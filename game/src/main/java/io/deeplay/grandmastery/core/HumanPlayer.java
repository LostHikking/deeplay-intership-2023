package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.exceptions.GameException;
import java.io.IOException;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param board Доска
   * @param color Цвет
   * @param ui UI
   */
  public HumanPlayer(String name, Board board, Color color, UI ui) {
    super(name, board, color, ui);
  }

  /** Метод, отвечающий за ввод хода игрока. */
  @Override
  public void makeMove(Board board) throws GameException {
    this.board = board;

    try {
      setMoveData(ui.inputMove(this.getName()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
