package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.domain.Color.WHITE;

import io.deeplay.grandmastery.figures.Piece;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** Класс-контейнер для графического интерфейса, хранит в себе компоненты. */
@Slf4j
@Getter
public class GuiContainer {

  private JButton offerDrawButton;
  private JButton surrenderButton;
  private JButton[][] cells;
  private JFrame frame;
  private JPanel chessPanel;
  private JPanel volumePanel;
  private JLabel[] bottomNumberLabels;
  private JLabel[] topNumberLabels;
  private JLabel[] rightNumberLabels;
  private JLabel[] leftNumberLabels;
  private JTextArea textArea;
  private boolean isWhitePlacement;
  private boolean isBlackPlacement;
  private JLabel movingPlayerLabel;
  private JTextArea logTextArea;
  private final Color originalBlackColor = new Color(81, 42, 42);
  private final Color originalWhiteColor = new Color(124, 76, 62);
  private List<String> eventMessages;

  public GuiContainer() {
    createGameGui();
  }

  /** Метод для инициализации всех компонент графического интерфейса. */
  private void createGameGui() {
    eventMessages = new ArrayList<>();
    loadCustomFont();
    // Создание окна и установка его позиции
    frame = new JFrame("Grandmastery");
    // Используем getResource для загрузки иконки из ресурсов
    ImageIcon icon =
        new ImageIcon(
            Objects.requireNonNull(
                getClass().getClassLoader().getResource("images/GrandmasteryIcon.png")));
    frame.setIconImage(icon.getImage());
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    int windowWidth = 630; // ширина окна
    int windowHeight = 550; // высота окна
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
    endGameButtonsPanel.setBounds(0, 0, windowWidth, 30);
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
    setBottomNumberLabels();
    setTopNumberLabels();
    setLeftNumberLabels();
    setRightNumberLabels();
    JScrollPane scrollPane = createTextArea();
    mainPanel.add(scrollPane);
    logTextArea = createLogTextArea(windowHeight, windowWidth, frame);
    mainPanel.add(logTextArea);
    // Создание и установка лейбла игрока, производящего ход
    movingPlayerLabel = createMovingPlayerLabel();
    mainPanel.add(movingPlayerLabel);
    // Установка панели громкости
    volumePanel = createVolumePanel();
    mainPanel.add(volumePanel);
    // Установка главной панели во фрейм
    frame.add(mainPanel);
  }

  /**
   * Метод инициализирует панель с кнопкой громкости.
   *
   * @return Панель.
   */
  public JPanel createVolumePanel() {
    // Создание панели и настройка макета.
    JPanel volumePanel = new JPanel();
    volumePanel.setBounds(445, 35, 160, 40);
    volumePanel.setBackground(new Color(245, 245, 220));

    // Создание метки "Звук:".
    JLabel soundLabel = new JLabel("Громкость:");

    // Создание кнопки с текущим изображением громкости.
    JButton volumeButton = new JButton();
    volumeButton.setPreferredSize(new Dimension(16, 16));
    volumeButton.setBackground(new Color(245, 245, 220));
    // Добавление компонентов на панель.
    volumePanel.add(soundLabel);
    volumePanel.add(volumeButton);
    return volumePanel;
  }

  void loadCustomFont() {
    try {
      // Создаем URL для ресурса шрифта
      URL fontUrl = getClass().getResource("/fonts/kanit.ttf");

      if (fontUrl == null) {
        log.error("Не удается загрузить файл шрифта.");
        return;
      }

      // Получаем InputStream на основе URL
      try (InputStream fontStream = fontUrl.openStream()) {
        if (fontStream == null) {
          log.error("Не удается загрузить файл шрифта.");
          return;
        }

        // Создание объекта Font
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);

        // Установка размера и стиля шрифта
        customFont = customFont.deriveFont(Font.PLAIN, 14);
        Color customColor = new Color(50, 20, 20);
        // Задаем кастомный шрифт глобально
        UIManager.getLookAndFeelDefaults().put("Label.font", customFont);
        UIManager.getLookAndFeelDefaults().put("Button.font", customFont);
        UIManager.getLookAndFeelDefaults().put("TextArea.font", customFont);
        UIManager.getLookAndFeelDefaults().put("TextField.font", customFont);
        UIManager.getLookAndFeelDefaults().put("Label.foreground", customColor);
        UIManager.getLookAndFeelDefaults().put("Button.foreground", customColor);
        UIManager.getLookAndFeelDefaults().put("TextArea.foreground", customColor);
        UIManager.getLookAndFeelDefaults().put("TextField.foreground", customColor);
      } catch (IOException e) {
        log.error("Проблемы с доступом к файлу со шрифтами.");
      }
    } catch (FontFormatException e) {
      log.error("Неверный формат шрифтов.");
    }
  }

  /**
   * Метод для инициализации TextArea.
   *
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

  /**
   * Метод для выбора ботов.
   * @param botsList Список ботов
   * @param color Цвет бота
   * @param parentFrame Родительское окно
   * @return Имя выбранного бота
   */
  public String showBotSelectionWindow(List<String> botsList,
                                       io.deeplay.grandmastery.domain.Color color,
                                       JFrame parentFrame) {
    JDialog dialog = new JDialog(parentFrame,
            "Выберите бота для "
                    + (color == io.deeplay.grandmastery.domain.Color.WHITE ? "белых:" : "черных:"),
            true);
    dialog.setLayout(new FlowLayout());
    ImageIcon icon =
            new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                            .getResource("images/GrandmasteryIcon.png")));
    dialog.setIconImage(icon.getImage());


    dialog.getContentPane().setBackground(new Color(245, 245, 220));

    JComboBox<String> botComboBox =
            new JComboBox<>(new DefaultComboBoxModel<>(botsList.toArray(new String[0])));
    JButton okButton = new JButton("OK");
    botComboBox.setBackground(new Color(213, 178, 156));
    okButton.setBackground(new Color(213, 178, 156));
    AtomicReference<String> selectedBot = new AtomicReference<>();

    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedBot.set((String) botComboBox.getSelectedItem());
        dialog.setVisible(false);
        dialog.dispose();
      }
    });

    dialog.add(botComboBox);
    dialog.add(okButton);

    dialog.setSize(new Dimension(230, 70));
    dialog.setResizable(false);

    dialog.setLocationRelativeTo(parentFrame);
    dialog.setVisible(true);

    return selectedBot.get();
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

    return JOptionPane.showOptionDialog(
        null,
        panel,
        "Выбор режима игры",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]);
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

    return JOptionPane.showOptionDialog(
        null,
        panel,
        "Выбор начальной расстановки шахмат",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]);
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
    return JOptionPane.showOptionDialog(
        null,
        panel,
        "Выбор цвета",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]);
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
    return JOptionPane.showOptionDialog(
        null,
        panel,
        "Подтверждение сдачи",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]);
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
    return JOptionPane.showOptionDialog(
        null,
        panel,
        "Подтверждение ничьей",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]);
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
    movingPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    return movingPlayerLabel;
  }

  /** Метод создания logTextArea. */
  public JTextArea createLogTextArea(int windowHeight, int windowWidth, JFrame parentFrame) {
    JTextArea logTextArea = new JTextArea();
    logTextArea.setBackground(new Color(225, 225, 200));

    logTextArea.setName("logTextArea");
    logTextArea.setEditable(false);
    logTextArea.setLineWrap(true);
    logTextArea.setWrapStyleWord(true);
    logTextArea.setBounds(20, windowHeight - 62, windowWidth - 50, 15);
    logTextArea.setVisible(true);

    // Add a MouseListener to handle the popup window
    logTextArea.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            showDialogWithTextArea(parentFrame);
          }
        });

    return logTextArea;
  }

  private void showDialogWithTextArea(JFrame parentFrame) {
    JDialog logDialog = new JDialog(parentFrame, "Логи", true);
    logDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    logDialog.setSize(400, 200);
    logDialog.setLocationRelativeTo(parentFrame);

    JTextArea logContentArea = new JTextArea();

    if (!eventMessages.isEmpty()) {
      StringBuilder logStringBuilder = new StringBuilder();
      for (String log : eventMessages) {
        logStringBuilder.append(log);
        logStringBuilder.append(System.lineSeparator());
      }
      logContentArea.setText(logStringBuilder.toString());
      updateEventMessages();
    }

    logContentArea.setEditable(false);
    logContentArea.setLineWrap(true);
    logContentArea.setWrapStyleWord(true);
    logContentArea.setBorder(new EmptyBorder(5, 5, 5, 5));

    JScrollPane scrollPane = new JScrollPane(logContentArea);
    logDialog.add(scrollPane);
    logDialog.setVisible(true);
  }

  /** Обновляет сообщения. */
  void updateEventMessages() {
    if (!eventMessages.isEmpty()) {
      String lastLog = eventMessages.get(eventMessages.size() - 1);
      logTextArea.setText(lastLog);
    } else {
      logTextArea.setText("");
    }
  }

  /**
   * Добавляет новое сообщение в logTextArea.
   *
   * @param message сообщение
   */
  public void addEventMessage(String message) {
    eventMessages.add(message);
    updateEventMessages();
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
        cells[row][col].setName(String.format("%d%d", row, col));
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
    x += 17;
    y += 405;
    bottomNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      bottomNumberLabels[i] = new JLabel();
      bottomNumberLabels[i].setBounds(x, y, 15, 15);
      bottomNumberLabels[i].setHorizontalAlignment(JLabel.CENTER);
      bottomNumberLabels[i].setVerticalAlignment(JLabel.CENTER);
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
    x += 17;
    y -= 20;
    topNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      topNumberLabels[i] = new JLabel();
      topNumberLabels[i].setBounds(x, y, 15, 15);
      topNumberLabels[i].setHorizontalAlignment(JLabel.CENTER);
      topNumberLabels[i].setVerticalAlignment(JLabel.CENTER);
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
    x += 405;
    y += 20;
    rightNumberLabels = new JLabel[8];
    for (int i = 0; i < 8; i++) {
      rightNumberLabels[i] = new JLabel();
      rightNumberLabels[i].setBounds(x, y, 15, 15);
      rightNumberLabels[i].setHorizontalAlignment(JLabel.LEFT);
      rightNumberLabels[i].setVerticalAlignment(JLabel.CENTER);
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
      leftNumberLabels[i].setBounds(x, y, 15, 15);
      leftNumberLabels[i].setHorizontalAlignment(JLabel.LEFT);
      leftNumberLabels[i].setVerticalAlignment(JLabel.CENTER);
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
    String imageName = "images/" + color + figureSymbol + ".png";

    try {
      URL imageUrl = getClass().getClassLoader().getResource(imageName);

      if (imageUrl != null) {
        ImageIcon icon = new ImageIcon(imageUrl);
        cellButton.setIcon(icon);
      } else {
        cellButton.setIcon(null);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
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
  @SuppressWarnings("StringSplitter")
  public String getLastLine() {
    String text = textArea.getText();
    if (text.isEmpty()) {
      return null;
    }
    String[] linesArray = text.split("\n");
    ArrayList<String> lines = new ArrayList<>(Arrays.asList(linesArray));
    return lines.get(lines.size() - 1).trim() + "\n";
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
