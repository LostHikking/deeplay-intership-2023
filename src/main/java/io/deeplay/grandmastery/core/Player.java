package io.deeplay.grandmastery.core;

/** Абстрактный класс, представлябщий игрока. */
public abstract class Player {
  /** Имя игрока. */
  private String name;
  /** Ход игрока в виде строки. */
  private String moveData;

  public Player(String name) {
    this.name = name;
  }

  /**
   * Метод, записывающий ход игрока.
   *
   * @param moveData Ход игрока
   * @throws IllegalArgumentException Если формат неправильный
   */
  public void setMoveData(String moveData) {
    if (!isValidMoveFormat(moveData)) {
      throw new IllegalArgumentException();
    }
    this.moveData = moveData;
  }

  public String getName() {
    return name;
  }

  public String getMoveData() {
    return moveData;
  }

  /**
   * Метод, проверяющий корректность хода.
   *
   * @param moveData Ход игрока. Пример: a2b2
   * @return true/false в зависимости от корректности
   */
  private boolean isValidMoveFormat(String moveData) {
    if (moveData.length() != 4) {
      return false;
    }
    char startCol = moveData.charAt(0);
    char startRow = moveData.charAt(1);
    char endCol = moveData.charAt(2);
    char endRow = moveData.charAt(3);
    if (!isValidColPosition(startCol)
        || !isValidColPosition(endCol)
        || !isValidRowPosition(startRow)
        || !isValidRowPosition(endRow)) {
      return false;
    }
    return true;
  }

  /**
   * Метод, проверяющий корректность символа строки в ходе.
   *
   * @param symbol Символ, который мы проверяем
   * @return true/false
   */
  private boolean isValidRowPosition(char symbol) {
    return (symbol >= '1' && symbol <= '8');
  }

  /**
   * Метод, проверяющий корректность символа столбца в ходе.
   *
   * @param symbol Символ, который мы проверяем
   * @return true/false
   */
  private boolean isValidColPosition(char symbol) {
    return (symbol >= 'a' && symbol <= 'h');
  }

  /** Абстрактный метод для реализации хода игрока. */
  public abstract void makeMove();
}
