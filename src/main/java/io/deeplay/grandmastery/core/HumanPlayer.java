package io.deeplay.grandmastery.core;

import java.util.Scanner;

/**
 * �������� ����� ������ Player, �������������� ��������� ������
 */
public class HumanPlayer extends Player{
    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * �����, ������� ��������� � ���������� ��� ������
     */
    public void makeMove() {
        Scanner scanner = new Scanner(System.in);
        while (true){
            try {
                System.out.print("������� ��� ���: ");
                setMoveData(scanner.nextLine());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("������������ ���. ����������, ������� ���������� ���.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}
