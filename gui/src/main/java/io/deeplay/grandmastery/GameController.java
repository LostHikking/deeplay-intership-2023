package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class GameController {
    @FXML
    private Button[][] buttons = new Button[8][8];
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;
    @FXML
    private TextArea textArea;

    @FXML
    private void initialize() {
        // Этот метод вызывается автоматически при загрузке FXML. Здесь я инициализирую каждую кнопку на доске.
        // Вообще этому классу как-то нужно прикрутить игру, чтобы у него был доступ к фигурам и я мог их как-то связать с кнопками
        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                Button button = buttons[row][col];
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(event -> handleButtonAction(finalRow, finalCol));
            }
        }
    }
    @FXML
    private void handleButtonAction(int row, int col) {
        // Этот метод вызывается при щелчке по кнопке.
        // Идея такова: есть нажимаемые кнопки (на которых есть фигуры), а есть ненажимаемые. Мы нажимаем на "нажимаемую'
        // кнопку и подсвечиваются возможные ходы. Теперь, если мы нажимаем на подсвеченную кнопку - совершается ход. Если
        // нажимаем какую-то другую - отмена хода.
    }
    @FXML
    private void handleOfferDraw()
    {
        //метод для предложения ничьи
    }
    @FXML
    private void handleSurrender()
    {
        //метод для сдачи
    }
    @FXML
    private void highlightPossibleMoves(Button sourceButton) {
        // Метод подсвечивания всех фигур, использующий getAllMoves
    }

    private void movePiece(Button sourceButton, Button targetButton) {
        //Здесь сам метод перемещения. Аля удаляем фигуру из source и перемещаем в таргет
    }

    @FXML
    private void setName(){
        //выводим имена игроков в лейблы
    }
}