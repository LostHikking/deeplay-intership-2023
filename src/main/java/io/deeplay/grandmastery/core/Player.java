package io.deeplay.grandmastery.core;

/**
 * ����������� �����, �������������� �����
 */
public abstract class Player {
    /**
     * ��� ������
     */
    private String name;
    /**
     * ������ � ����� ������
     */
    private String moveData;

    public Player(String name) {
        this.name = name;
    }

    /**
     * ������ ����,����� ������������ ������������ ��������
     * @param moveData
     */
    public void setMoveData(String moveData) {
        if (!isValidMoveFormat(moveData)) {
            throw new IllegalArgumentException("������������ ������ ����. ����������� ������ 'a2a4'!");
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
     * �����, ����������� ������������ ���������� ���� ������
     * @param moveData ��� ������. ������: a2b2
     * @return true/false � ����������� �� ������������
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
     * �����, ����������� ������������ ��������� ����������� � ������ ����
     * @param symbol ���� �� �������� ����������� ����
     * @return true/false
     */
    private boolean isValidRowPosition(char symbol) {
        return(symbol >= '1' && symbol <= '8');
    }

    /**
     * �����, ����������� ������������ ��������� ��������� � ������ ����
     * @param symbol ���� �� �������� ��������� ����
     * @return true/false
     */
    private boolean isValidColPosition(char symbol) {
        return (symbol >= 'a' && symbol <= 'h');
    }

    /**
     * ����������� ����� ��� ���������� ���� �������
     */
    public abstract void makeMove();

}
