package io.deeplay.grandmastery.core;

import java.util.Scanner;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  public HumanPlayer(String name) {
    super(name);
  }

  /** Метод, отвечающий за ввод хода игрока. */
  @Override
  public void makeMove() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      try {
        System.out.print("Введите ваш ход: ");
        setMoveData(scanner.nextLine());
        break;
      } catch (IllegalArgumentException e) {
        System.out.println("Некорректный ход! Пожалуйста, введите ход правильно.");
      }
    }
  }
}
