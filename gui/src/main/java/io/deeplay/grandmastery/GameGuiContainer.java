package io.deeplay.grandmastery;

import io.deeplay.grandmastery.figures.Piece;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import static io.deeplay.grandmastery.domain.Color.WHITE;

/** Класс-контейнер для графического интерфейса, хранит в себе компоненты. */
@Getter
public class GameGuiContainer {
  private JButton offerDrawButton;
  private JButton surrenderButton;
  private JButton[][] cells;
  private JFrame frame;
  private JPanel chessPanel;
  private JTextArea textArea;
  private JLabel movingPlayerLabel;
  private final Color originalBlackColor = new Color(81, 42, 42);
  private final Color originalWhiteColor = new Color(124, 76, 62);

  public GameGuiContainer() {
    createGameGui();
  }

  /** Метод для инициализации всех компонент графического интерфейса */
  private void createGameGui() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Вычисляем координаты для размещения окна по центру экрана
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;
    int windowWidth = 630; // ширина окна
    int windowHeight = 530; // высота окна
    int windowX = (screenWidth - windowWidth) / 2;
    int windowY = (screenHeight - windowHeight) / 2;
    // Создание окна и установка его позиции
    frame = new JFrame("Chess Board");
    ImageIcon icon =
        new ImageIcon(
            "C:/Users/Матвей/IdeaProjects/grandmastery/gui/src/main/resources/GrandmasteryIcon.png"); // Замените "path/to/icon.png" путем к вашей иконке
    frame.setIconImage(icon.getImage());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(windowWidth, windowHeight);
    frame.setLocation(windowX, windowY);
    frame.setResizable(false);
    // Установка главной панели
    JPanel mainPanel = new JPanel(null);
    mainPanel.setBackground(new Color(245, 245, 220));
    // Создание и панели с кнопками завершения игры
    JPanel endGameButtonsPanel = createEndGameButtonsPanel();
    endGameButtonsPanel.setBounds(0, 0, 630, 30);
    mainPanel.add(endGameButtonsPanel);
    // Создание и установка шахматной доски
    JPanel chessPanel = createChessPanel();
    int x = 25;
    int y = 60;
    chessPanel.setBounds(x, y, 400, 400);
    mainPanel.add(chessPanel);
    createTopNumberLabels(mainPanel, x, y);
    createRightNumberLabels(mainPanel, x, y);
    createBottomNumberLabels(mainPanel, x, y);
    createLeftNumberLabels(mainPanel, x, y);
    // Создание и установка текстового поля для вывода хода игры
    textArea = new JTextArea();
    textArea.setBackground(new Color(222, 184, 135));
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setBounds(450, 60, 160, 400);
    textArea.setFocusable(false);
    mainPanel.add(scrollPane);
    // Создание и установка лейбла игрока, производящего ход
    JLabel movingPlayerLabel = createMovingPlayerLabel();
    mainPanel.add(movingPlayerLabel);
    // Установка главной панели во фрейм
    frame.add(mainPanel);
  }

  /** Метод для вывода игрового окна. */
  public void showGameFrame() {
    frame.setVisible(true);
  }

  /**
   * Метод для вывода сообщений в TextArea.
   *
   * @param message Сообщение
   */
  public void printMessage(String message) {
    this.textArea.append(message);
  }

  /**
   * Метод для создания лейбла с отображением ходящего игрока
   *
   * @return Лейбл
   */
  public JLabel createMovingPlayerLabel() {
    movingPlayerLabel = new JLabel();
    movingPlayerLabel.setBounds(450, 465, 160, 15);
    Font font = new Font("Arial", Font.BOLD, 15);
    movingPlayerLabel.setFont(font);
    movingPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    return movingPlayerLabel;
  }

  /**
   * Метод для обновления сообщения.
   *
   * @param message Нужный текст
   */
  public void setLabelMessage(String message) {
    movingPlayerLabel.setText(message);
  }
  /**
   * Метод инициализации панели с кнопками завершения игры.
   *
   * @return Панель с кнопками завершения игры.
   */
  private JPanel createEndGameButtonsPanel() {
    JPanel endGameButtonsPanel = new JPanel(null);
    offerDrawButton = new JButton("Предложить ничью");
    surrenderButton = new JButton("Сдаться");
    offerDrawButton.setBounds(0, 0, 308, 30);
    surrenderButton.setBounds(308, 0, 308, 30);
    offerDrawButton.setBackground(new Color(213, 178, 156));
    surrenderButton.setBackground(new Color(213, 178, 156));
    endGameButtonsPanel.add(offerDrawButton);
    endGameButtonsPanel.add(surrenderButton);
    return endGameButtonsPanel;
  }
  /**
   * Метод, инициализирующий шахматную доску.
   *
   * @return Панель с шахматной доской
   */
  private JPanel createChessPanel() {
    chessPanel = new JPanel(null);
    chessPanel.setPreferredSize(new Dimension(400, 420));

    int cellSize = 50;
    int columnSpacing = 0;
    cells = new JButton[8][8];

    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        int x = col * (cellSize + columnSpacing);
        int y = row * cellSize;
        JButton cellsButton = new JButton();
        cellsButton.setBounds(x, y, cellSize, cellSize);
        cells[row][col] = cellsButton;

        if ((row + col) % 2 == 0) {
          cellsButton.setBackground(originalBlackColor);
        } else {
          cellsButton.setBackground(originalWhiteColor);
        }
        chessPanel.add(cellsButton);
      }
    }

    return chessPanel;
  }

  public JButton getCell(int col, int row) {
    return cells[row][col];
  }

  /**
   * Создаёт нумерацию слева от доски.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createLeftNumberLabels(JPanel mainPanel, int x, int y) {
    x -= 15;
    y += 20;
    for (int i = 1; i <= 8; i++) {
      JLabel label = new JLabel();
      label.setText(Integer.toString(i));
      label.setBounds(x, y, 10, 10);
      mainPanel.add(label);
      y += 50;
    }
  }
  /**
   * Создаёт нумерацию под доской.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createBottomNumberLabels(JPanel mainPanel, int x, int y) {
    x += 20;
    y += 410;
    String str = "abcdefgh";
    for (int i = 0; i < 8; i++) {
      JLabel label = new JLabel();
      label.setText(String.valueOf(str.charAt(i)));
      label.setBounds(x, y, 10, 10);
      mainPanel.add(label);
      x += 50;
    }
  }
  /**
   * Создаёт нумерацию над доской.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createTopNumberLabels(JPanel mainPanel, int x, int y) {
    x += 20;
    y -= 20;
    String str = "abcdefgh";
    for (int i = 0; i < 8; i++) {
      JLabel label = new JLabel();
      label.setText(String.valueOf(str.charAt(i)));
      label.setBounds(x, y, 10, 10);
      mainPanel.add(label);
      x += 50;
    }
  }
  /**
   * Создаёт нумерацию справа от доски.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createRightNumberLabels(JPanel mainPanel, int x, int y) {
    x += 410;
    y += 20;
    for (int i = 0; i < 8; i++) {
      JLabel label = new JLabel();
      label.setText(String.valueOf(i + 1));
      label.setBounds(x, y, 10, 10);
      mainPanel.add(label);
      y += 50;
    }
  }

  /**
   * Метод установки иконки фигурки на кнопку.
   *
   * @param piece Фигура, которую мы хотим вывести.
   * @param col Столбец
   * @param row Строка
   */
  public void setPieceIcon(Piece piece, int col, int row) {
    JButton cellButton = cells[row][col];
    char figureSymbol = piece.getFigureType().getSymbol();
    String color;
    if (piece.getColor() == WHITE) {
      color = "White";
    } else {
      color = "Black";
    }
    String imagePath = "C:/Users/Матвей/IdeaProjects/grandmastery/gui/src/main/resources/";
    switch (figureSymbol) {
      case 'k':
        ImageIcon kingIcon = new ImageIcon(imagePath + color + "King.png");
        cellButton.setIcon(kingIcon);
        break;
      case 'q':
        ImageIcon queenIcon = new ImageIcon(imagePath + color + "Queen.png");
        cellButton.setIcon(queenIcon);
        break;
      case 'r':
        ImageIcon rookIcon = new ImageIcon(imagePath + color + "Rook.png");
        cellButton.setIcon(rookIcon);
        break;
      case 'b':
        ImageIcon bishopIcon = new ImageIcon(imagePath + color + "Bishop.png");
        cellButton.setIcon(bishopIcon);
        break;
      case 'n':
        ImageIcon knightIcon = new ImageIcon(imagePath + color + "Knight.png");
        cellButton.setIcon(knightIcon);
        break;
      case 'p':
        ImageIcon pawnIcon = new ImageIcon(imagePath + color + "Pawn.png");
        cellButton.setIcon(pawnIcon);
        break;
      default:
        cellButton.setIcon(null);
        break;
    }
  }

  /**
   * Удаление иконки фигурки с клетки
   *
   * @param col Столбец.
   * @param row Строка.
   */
  public void removePieceIcon(int col, int row) {
    cells[row][col].setIcon(null);
  }

  /**
   * Метод подсветки кнопки.
   *
   * @param col Столбец.
   * @param row Строка.
   */
  public void highlightCell(int col, int row) {
    cells[row][col].setBackground(new java.awt.Color(0, 255, 0));
  }
  /**
   * Метод слабой подсветки кнопки(для той которая ходит).
   *
   * @param col Столбец.
   * @param row Строка.
   */
  public void smallHighlightCell(int col, int row) {
    cells[row][col].setBackground(new java.awt.Color(0, 50, 0));
  }

  public static void main(String[] args) {
    GameGui gui = new GameGui();
    gui.showGui();
  }
}
