package io.deeplay.grandmastery.core;

/**
 * Абстрактный класс, представляющий игрок
 */
public abstract class Player {
    /**
     * Имя игрока
     */
    private String name;
    /**
     * Строка с ходом игрока
     */
    private String moveData;

    public Player(String name) {
        this.name = name;
    }

    /**
     * Сеттер хода,также обрабатывает некорректное значение
     * @param moveData
     */
    public void setMoveData(String moveData) {
        if (!isValidMoveFormat(moveData)) {
            throw new IllegalArgumentException("Некорректный формат хода. Используйте формат 'a2a4'!");
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
     * Метод, проверяющий корректность синтаксиса хода игрока
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
        if (!isValidColPosition(startCol) || !isValidColPosition(endCol) ||
                !isValidRowPosition(startRow) || !isValidRowPosition(endRow)) {
            return false;
        }
        return true;
    }

    /**
     * Метод, проверяющий корректность введенной горизонтали в строке хода
     * @param symbol Один из символов горизонтали хода
     * @return true/false
     */
    private boolean isValidRowPosition(char symbol) {
        return(symbol >= '1' && symbol <= '8');
    }

    /**
     * Метод, проверяющий корректность введенной вертикали в строке хода
     * @param symbol Один из символов вертикали хода
     * @return true/false
     */
    private boolean isValidColPosition(char symbol) {
        return (symbol >= 'a' && symbol <= 'h');
    }

    /**
     * Абстрактный метод для совершения хода игроком
     */
    public abstract void makeMove();

}
