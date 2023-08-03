package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.exceptions.GameException;
import java.nio.charset.Charset;
import java.util.Scanner;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  public HumanPlayer(String name) {
    super(name);
  }

  /** Метод, отвечающий за ввод хода игрока. */
  @Override
  public boolean makeMove() {
    try (Scanner scanner = new Scanner(System.in, Charset.defaultCharset())) {
      System.out.print("Введите ваш ход: ");
      setMoveData(scanner.nextLine());
      return true;
    } catch (GameException e) {
      System.out.println("Некорректный ход! Пожалуйста, введите ход правильно.");
      return false;
    }
  }
}
