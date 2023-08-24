package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.domain.Color.WHITE;

import io.deeplay.grandmastery.figures.Piece;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import lombok.Getter;

/** Класс-контейнер для графического интерфейса, хранит в себе компоненты. */
@Getter
public class GuiContainer {

  private JButton offerDrawButton;
  private JButton surrenderButton;
  private JButton[][] cells;
  private JFrame frame;
  private JPanel chessPanel;
  private JLabel[] bottomNumberLabels;
  private JLabel[] topNumberLabels;
  private JLabel[] rightNumberLabels;
  private JLabel[] leftNumberLabels;
  private JTextArea textArea;
  private boolean isWhitePlacement;
  private boolean isBlackPlacement;
  private JLabel movingPlayerLabel;
  private final Color originalBlackColor = new Color(81, 42, 42);
  private final Color originalWhiteColor = new Color(124, 76, 62);

  public GuiContainer() {
    createGameGui();
  }

  /** Метод для инициализации всех компонент графического интерфейса. */
  private void createGameGui() {
    // Создание окна и установка его позиции
    frame = new JFrame("Chess Board");
    // Используем getResource для загрузки иконки из ресурсов
    ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("GrandmasteryIcon.png"));
    frame.setIconImage(icon.getImage());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    int windowWidth = 630; // ширина окна
    int windowHeight = 530; // высота окна
    frame.setSize(windowWidth, windowHeight);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;
    int windowX = (screenWidth - windowWidth) / 2;
    int windowY = (screenHeight - windowHeight) / 2;
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
    JScrollPane scrollPane = createTextArea();
    mainPanel.add(scrollPane);
    // Создание и установка лейбла игрока, производящего ход
    JLabel movingPlayerLabel = createMovingPlayerLabel();
    mainPanel.add(movingPlayerLabel);
    // Установка главной панели во фрейм
    frame.add(mainPanel);
  }

  /**
   * Метод для инициализации TextArea.
   * @return Панель с textArea
   */
  public JScrollPane createTextArea() {
    // Создание и установка текстового поля для вывода хода игры
    textArea = new JTextArea();
    textArea.setBackground(new Color(222, 184, 135));
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setBounds(450, 60, 160, 400);
    textArea.setFocusable(false);
    textArea.setName("textArea");
    return scrollPane;
  }

  /** Метод выводит диалоговое окно для выбора режима игры. */
  public int showModeSelectionWindow() {

    // Создаем панель для выравнивания компонентов.
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Создаем компонент с выравниванием по центру.
    JLabel messageLabel = new JLabel("Выберите режим игры:");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Добавляем компоненты на панель.
    panel.add(Box.createVerticalGlue()); // Пространство сверху
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue()); // Пространство снизу
    Object[] options = {"Human vs Bot", "Human vs Human", "Bot vs Bot"};
    int choice =
        JOptionPane.showOptionDialog(
            null,
            panel,
            "Выбор режима игры",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);

    return choice;
  }

  /** Метод выводит диалоговое окно для выбора начальной расстановки. */
  public int showChessTypeSelectionWindow() {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel messageLabel = new JLabel("Выберите начальную расстановку:");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    messageLabel.setOpaque(true);

    panel.add(Box.createVerticalGlue());
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue());

    Object[] options = {"Классические", "Фишера"};
    int choice =
        JOptionPane.showOptionDialog(
            null,
            panel,
            "Выбор начальной расстановки шахмат",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);

    return choice;
  }

  /** Метод выводит диалоговое окно для выбора цвета. */
  public int showColorSelectionWindow() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    JLabel messageLabel = new JLabel("Выберите цвет:");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    messageLabel.setOpaque(true);
    panel.add(Box.createVerticalGlue());
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue());
    Object[] options = {"Белый", "Черный"};
    int choice =
        JOptionPane.showOptionDialog(
            null,
            panel,
            "Выбор цвета",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);
    return choice;
  }

  /** Метод выводит диалоговое окно для подтверждения сдачи. */
  public int showConfirmSurWindow() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel messageLabel = new JLabel("Вы хотите сдаться?");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    panel.add(Box.createVerticalGlue());
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue());
    Object[] options = {"Да", "Нет"};
    int choice =
        JOptionPane.showOptionDialog(
            null,
            panel,
            "Подтверждение сдачи",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);
    return choice;
  }

  /** Метод выводит диалоговое окно для подтверждения ничьей. */
  public int showAnswerDrawWindow() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel messageLabel = new JLabel("Вам предложили ничью. Ваш ответ?");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    panel.add(Box.createVerticalGlue());
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue());

    Object[] options = {"Отказываюсь", "Соглашаюсь"};
    int choice =
        JOptionPane.showOptionDialog(
            null,
            panel,
            "Подтверждение ничьей",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);
    return choice;
  }

  /** Метод выводит диалоговое окно для ввода имени игрока. */
  public String showInputPlayerNameWindow(io.deeplay.grandmastery.domain.Color color) {
    JTextField textField = new JTextField(20);
    textField.setAlignmentX(Component.CENTER_ALIGNMENT);
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(Box.createVerticalGlue());
    String message =
        (color == io.deeplay.grandmastery.domain.Color.WHITE ? "Белый" : "Черный")
            + " игрок, введите ваше имя:";
    panel.add(new JLabel(message));
    panel.add(Box.createVerticalStrut(10));
    panel.add(textField);
    panel.add(Box.createVerticalGlue());
    int option =
        JOptionPane.showConfirmDialog(
            null,
            panel,
            "Ввод имени игрока",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

    if (option == JOptionPane.OK_OPTION) {
      return textField.getText();
    } else {
      return null;
    }
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
   * Метод для создания лейбла с отображением ходящего игрока.
   *
   * @return Лейбл
   */
  public JLabel createMovingPlayerLabel() {
    movingPlayerLabel = new JLabel();
    movingPlayerLabel.setName("movingPlayerLabel");
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
    offerDrawButton = new JButton("Предложить ничью");
    offerDrawButton.setName("offerDrawButton");
    surrenderButton = new JButton("Сдаться");
    surrenderButton.setName("surrenderButton");
    offerDrawButton.setBounds(0, 0, 308, 30);
    surrenderButton.setBounds(308, 0, 308, 30);
    offerDrawButton.setBackground(new Color(213, 178, 156));
    surrenderButton.setBackground(new Color(213, 178, 156));
    JPanel endGameButtonsPanel = new JPanel(null);
    endGameButtonsPanel.add(offerDrawButton);
    endGameButtonsPanel.add(surrenderButton);
    endGameButtonsPanel.setName("EndGameButtonsPanel");
    return endGameButtonsPanel;
  }

  /**
   * Метод, инициализирующий шахматную доску.
   *
   * @return Панель с шахматной доской
   */
  private JPanel createChessPanel() {
    chessPanel = new JPanel(null);
    chessPanel.setName("chessPanel");
    chessPanel.setPreferredSize(new Dimension(400, 420));

    int cellSize = 50;
    int columnSpacing = 0;
    cells = new JButton[8][8];

    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        int x = (7 - col) * (cellSize + columnSpacing);
        int y = row * cellSize;
        JButton cellsButton = new JButton();
        cellsButton.setBounds(x, y, cellSize, cellSize);
        cells[row][col] = cellsButton;
        cells[row][col].setName(Integer.toString(row) + Integer.toString(col));
        if ((row + col) % 2 == 0) {
          cellsButton.setBackground(originalBlackColor);
        } else {
          cellsButton.setBackground(originalWhiteColor);
        }
        chessPanel.add(cellsButton);
      }
    }
    isBlackPlacement = true;
    isWhitePlacement = false;
    return chessPanel;
  }

  /** Метод, вращающий наш массив кнопок. */
  public void rotateArray() {
    int rows = cells.length;
    int cols = cells[0].length;
    for (int i = 0; i < rows / 2; i++) {
      for (int j = 0; j < cols; j++) {
        JButton temp = cells[i][j];
        cells[i][j] = cells[rows - i - 1][cols - j - 1];
        cells[rows - i - 1][cols - j - 1] = temp;
      }
    }

    if (rows % 2 != 0) {
      for (int j = 0; j < cols / 2; j++) {
        JButton temp = cells[rows / 2][j];
        cells[rows / 2][j] = cells[rows / 2][cols - j - 1];
        cells[rows / 2][cols - j - 1] = temp;
      }
    }

    isWhitePlacement = !isWhitePlacement;
    isBlackPlacement = !isBlackPlacement;
    setBottomNumberLabels();
    setTopNumberLabels();
    setLeftNumberLabels();
    setRightNumberLabels();
  }

  public JButton getCell(int col, int row) {
    return cells[row][col];
  }

  /**
   * Создаёт лейблы для нумерации под доской.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createBottomNumberLabels(JPanel mainPanel, int x, int y) {
    x += 20;
    y += 410;
    bottomNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      bottomNumberLabels[i] = new JLabel();
      bottomNumberLabels[i].setBounds(x, y, 10, 10);
      mainPanel.add(bottomNumberLabels[i]);
      x += 50;
    }
  }

  /**
   * Создаёт лейблы для нумерации над доской.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createTopNumberLabels(JPanel mainPanel, int x, int y) {
    x += 20;
    y -= 20;
    topNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      topNumberLabels[i] = new JLabel();
      topNumberLabels[i].setBounds(x, y, 10, 10);
      mainPanel.add(topNumberLabels[i]);
      x += 50;
    }
  }

  /**
   * Создаёт лейблы для нумерации справа от доски.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createRightNumberLabels(JPanel mainPanel, int x, int y) {
    x += 410;
    y += 20;
    rightNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      rightNumberLabels[i] = new JLabel();
      rightNumberLabels[i].setBounds(x, y, 10, 10);
      mainPanel.add(rightNumberLabels[i]);
      y += 50;
    }
  }

  /**
   * Создаёт лейблы для нумерации слева от доски.
   *
   * @param mainPanel Панель шахматной доски
   * @param x Координата панели
   * @param y Координата панели
   */
  public void createLeftNumberLabels(JPanel mainPanel, int x, int y) {
    x -= 15;
    y += 20;
    leftNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      leftNumberLabels[i] = new JLabel();
      leftNumberLabels[i].setBounds(x, y, 10, 10);
      mainPanel.add(leftNumberLabels[i]);
      y += 50;
    }
  }

  /** Устанавливает нумерацию сверху. */
  public void setTopNumberLabels() {
    String str;
    if (isWhitePlacement) {
      str = "abcdefgh";
    } else {
      str = "hgfedcba";
    }
    for (int i = 0; i < 8; i++) {
      topNumberLabels[i].setText(String.valueOf(str.charAt(i)));
    }
  }

  /** Устанавливает нумерацию снизу. */
  public void setBottomNumberLabels() {
    String str;
    if (isWhitePlacement) {
      str = "abcdefgh";
    } else {
      str = "hgfedcba";
    }
    for (int i = 0; i < 8; i++) {
      bottomNumberLabels[i].setText(String.valueOf(str.charAt(i)));
    }
  }

  /** Устанавливает нумерацию справа. */
  public void setRightNumberLabels() {
    String str;
    if (isWhitePlacement) {
      str = "87654321";
    } else {
      str = "12345678";
    }
    for (int i = 0; i < 8; i++) {
      rightNumberLabels[i].setText(String.valueOf(str.charAt(i)));
    }
  }

  /** Устанавливает нумерацию слева. */
  public void setLeftNumberLabels() {
    String str;
    if (isWhitePlacement) {
      str = "87654321";
    } else {
      str = "12345678";
    }
    for (int i = 0; i < 8; i++) {
      leftNumberLabels[i].setText(String.valueOf(str.charAt(i)));
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
    char figureSymbol =
        Character.toUpperCase(
            piece.getFigureType().getSymbol()); // Преобразуем символ в большую букву
    String color;
    if (piece.getColor() == WHITE) {
      color = "White";
    } else {
      color = "Black";
    }
    String imageName = color + figureSymbol + ".png";

    try {
      URL imageUrl = getClass().getClassLoader().getResource(imageName);

      if (imageUrl != null) {
        ImageIcon icon = new ImageIcon(imageUrl);
        cellButton.setIcon(icon);
      } else {
        cellButton.setIcon(null);
      }
    } catch (Exception e) {
      e.printStackTrace();
      cellButton.setIcon(null);
    }
  }

  /**
   * Удаление иконки фигурки с клетки.
   *
   * @param col Столбец.
   * @param row Строка.
   */
  public void removePieceIcon(int col, int row) {
    cells[row][col].setIcon(null);
  }

  /**
   * Метод возвращает последнюю строку в textArea.
   *
   * @return Последняя строка.
   */
  public String getLastLine() {
    String text = textArea.getText();
    if (text.isEmpty()) {
      return null;
    }
    String[] lines = text.split("\n");
    return lines[lines.length - 1].trim() + "\n";
  }

  /**
   * Метод подсветки кнопки.
   *
   * @param col Столбец.
   * @param row Строка.
   */
  public void highlightCell(int col, int row) {
    cells[row][col].setBackground(new Color(0, 255, 0));
  }

  /**
   * Метод слабой подсветки кнопки(для той которая ходит).
   *
   * @param col Столбец.
   * @param row Строка.
   */
  public void smallHighlightCell(int col, int row) {
    cells[row][col].setBackground(new Color(0, 50, 0));
  }
}
