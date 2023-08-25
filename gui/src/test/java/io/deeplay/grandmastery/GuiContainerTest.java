package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.domain.Color.BLACK;
import static io.deeplay.grandmastery.domain.Color.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.assertj.swing.assertions.Assertions;
import org.junit.jupiter.api.Test;

class GuiContainerTest {

  private JButton[][] cells;

  private GuiContainer guiContainer;

  public void init() {
    guiContainer = new GuiContainer();
    cells = guiContainer.getCells();
  }

  private <T extends Component> T findComponentByName(
      Container container, Class<T> componentType, String name) {
    for (Component component : container.getComponents()) {
      if (componentType.isInstance(component) && name.equals(component.getName())) {
        return componentType.cast(component);
      } else if (component instanceof Container) {
        T foundComponent = findComponentByName((Container) component, componentType, name);
        if (foundComponent != null) {
          return foundComponent;
        }
      }
    }
    return null;
  }

  @Test
  void testAddLogWithEmptyLogs() {
    init();
    guiContainer.addLog("Test log");
    String lastLog =
        guiContainer.getLogs().isEmpty()
            ? ""
            : guiContainer.getLogs().get(guiContainer.getLogs().size() - 1);

    assertEquals("Test log", lastLog);
    assertEquals("Test log", guiContainer.getLogTextArea().getText());
  }

  @Test
  void testAddLogWithNonEmptyLogs() {
    init();
    guiContainer.addLog("First log");
    guiContainer.addLog("Second log");

    String lastLog =
        guiContainer.getLogs().isEmpty()
            ? ""
            : guiContainer.getLogs().get(guiContainer.getLogs().size() - 1);

    assertEquals("Second log", lastLog);
    assertEquals("Second log", guiContainer.getLogTextArea().getText());
  }

  @Test
  void testUpdateLogWithEmptyLogs() {
    init();
    guiContainer.addLog("Test log");
    guiContainer.getLogs().clear();
    guiContainer.updateLog();

    assertEquals("", guiContainer.getLogTextArea().getText());
  }

  @Test
  void testUpdateLogWithNonEmptyLogs() {
    init();
    guiContainer.addLog("First log");
    guiContainer.addLog("Second log");
    guiContainer.updateLog();

    assertEquals("Second log", guiContainer.getLogTextArea().getText());
  }

  @Test
  public void testCreateVolumePanel() {
    init();
    JPanel volumePanel = guiContainer.createVolumePanel();

    assertNotNull(volumePanel, "Панель громкости не должна быть null");

    assertEquals(
            new Rectangle(445, 35, 160, 40),
            volumePanel.getBounds(),
            "Границы панели громкости должны быть корректными.");
    assertEquals(
            new Color(245, 245, 220),
            volumePanel.getBackground(),
            "Цвет фона панели громкости должен быть корректным.");

    Component[] components = volumePanel.getComponents();
    assertEquals(2, components.length, "Панель громкости должна содержать 2 компонента.");

    JLabel soundLabel;
    JButton volumeButton;
    if (components[0] instanceof JLabel) {
      soundLabel = (JLabel) components[0];
      volumeButton = (JButton) components[1];
    } else {
      soundLabel = (JLabel) components[1];
      volumeButton = (JButton) components[0];
    }

    assertEquals("Громкость:", soundLabel.getText(), "Текст метки звука должен быть корректным.");

    assertEquals(
            new Dimension(16, 16),
            volumeButton.getPreferredSize(),
            "Предпочтительный размер кнопки громкости должен быть корректным.");
  }

  @Test
  public void testCreateLogTextArea() {
    init();
    int windowHeight = 500;
    int windowWidth = 800;
    JFrame parentFrame = new JFrame();

    JTextArea logTextArea = guiContainer.createLogTextArea(windowHeight, windowWidth, parentFrame);

    assertNotNull(logTextArea, "Область текста журнала не должна быть null");

    assertEquals(
        new Color(225, 225, 200),
        logTextArea.getBackground(),
        "Цвет фона области текста журнала должен быть корректным.");
    assertEquals(
        "logTextArea", logTextArea.getName(), "Имя области текста журнала должно быть корректным.");
    assertFalse(
        logTextArea.isEditable(),
        "Область текста журнала не должна быть доступна для редактирования.");
    assertTrue(
        logTextArea.getLineWrap(), "Область текста журнала должна иметь включенное перенос слов.");
    assertTrue(
        logTextArea.getWrapStyleWord(),
        "Область текста журнала должна иметь включенный стиль переноса слов.");
    assertTrue(logTextArea.isVisible(), "Область текста журнала должна быть видимой.");

    boolean hasCorrectMouseListener = false;
    for (MouseListener listener : logTextArea.getMouseListeners()) {
      if (listener instanceof MouseAdapter) {
        hasCorrectMouseListener = true;
        break;
      }
    }
    assertTrue(
        hasCorrectMouseListener,
        "Область текста журнала должна иметь MouseAdapter в качестве MouseListener.");
  }

  @Test
  public void testLoadCustomFont() {
    GuiContainer guiContainer = new GuiContainer();
    guiContainer.loadCustomFont("/fonts/kanit.ttf");

    
    Font labelFont = UIManager.getLookAndFeelDefaults().getFont("Label.font");
    assertNotNull(labelFont);
    Font buttonFont = UIManager.getLookAndFeelDefaults().getFont("Button.font");
    assertNotNull(buttonFont);
    Font textAreaFont = UIManager.getLookAndFeelDefaults().getFont("TextArea.font");
    assertNotNull(textAreaFont);
    Font textFieldFont = UIManager.getLookAndFeelDefaults().getFont("TextField.font");
    assertNotNull(textFieldFont);

    assertEquals("Kanit Cyrillic", labelFont.getFontName());
    assertEquals("Kanit Cyrillic", buttonFont.getFontName());
    assertEquals("Kanit Cyrillic", textAreaFont.getFontName());
    assertEquals("Kanit Cyrillic", textFieldFont.getFontName());

    assertEquals(Font.PLAIN, labelFont.getStyle());
    assertEquals(Font.PLAIN, buttonFont.getStyle());
    assertEquals(Font.PLAIN, textAreaFont.getStyle());
    assertEquals(Font.PLAIN, textFieldFont.getStyle());

    assertEquals(14, labelFont.getSize());
    assertEquals(14, buttonFont.getSize());
    assertEquals(14, textAreaFont.getSize());
    assertEquals(14, textFieldFont.getSize());

    Color labelColor = UIManager.getLookAndFeelDefaults().getColor("Label.foreground");
    assertNotNull(labelColor);
    Color buttonColor = UIManager.getLookAndFeelDefaults().getColor("Button.foreground");
    assertNotNull(buttonColor);
    Color textAreaColor = UIManager.getLookAndFeelDefaults().getColor("TextArea.foreground");
    assertNotNull(textAreaColor);
    Color textFieldColor = UIManager.getLookAndFeelDefaults().getColor("TextField.foreground");
    assertNotNull(textFieldColor);

    assertEquals(new Color(50, 20, 20), labelColor);
    assertEquals(new Color(50, 20, 20), buttonColor);
    assertEquals(new Color(50, 20, 20), textAreaColor);
    assertEquals(new Color(50, 20, 20), textFieldColor);
  }

  @Test
  public void testCreateGameGui() {
    init();
    JFrame frame = guiContainer.getFrame();
    JLabel label = findComponentByName(frame, JLabel.class, "movingPlayerLabel");
    JTextArea textArea = findComponentByName(frame, JTextArea.class, "textArea");
    JButton offerDrawButton = findComponentByName(frame, JButton.class, "offerDrawButton");
    JButton surrenderButton = findComponentByName(frame, JButton.class, "surrenderButton");
    JPanel chessPanel = findComponentByName(frame, JPanel.class, "chessPanel");

    Assertions.assertThat(label).isNotNull();
    Assertions.assertThat(textArea).isNotNull();
    Assertions.assertThat(offerDrawButton).isNotNull();
    Assertions.assertThat(surrenderButton).isNotNull();
    Assertions.assertThat(chessPanel).isNotNull();
  }

  @Test
  public void testPrintMessage() {
    init();
    String message = "Moto: a2a4";
    guiContainer.printMessage(message);
    String actual = guiContainer.getTextArea().getText();
    assertEquals(message, actual);
  }

  @Test
  public void testRotateArray() {
    init();
    JButton[][] buttons;
    buttons = guiContainer.getCells();
    JButton[][] copiedButtons = new JButton[8][8];
    for (int i = 0; i < 8; i++) {
      System.arraycopy(buttons[i], 0, copiedButtons[i], 0, 8);
    }

    guiContainer.rotateArray();
    JButton[][] rotatedButtons;
    rotatedButtons = guiContainer.getCells();

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        assertEquals(copiedButtons[i][j].getName(), rotatedButtons[7 - i][7 - j].getName());
      }
    }
  }

  @Test
  public void setRightNumberLabelsTest() {
    init();
    guiContainer.setRightNumberLabels();
    String expected = guiContainer.isBlackPlacement() ? "12345678" : "87654321";
    StringBuilder actual = new StringBuilder();
    JLabel[] labels = guiContainer.getRightNumberLabels();
    for (int i = 0; i < 8; i++) {
      actual.append(labels[i].getText());
    }
    assertEquals(expected, actual.toString());
  }

  @Test
  public void setLeftNumberLabelsTest() {
    init();
    guiContainer.setLeftNumberLabels();
    String expected = guiContainer.isBlackPlacement() ? "12345678" : "87654321";
    String actual = new String();
    JLabel[] labels = guiContainer.getLeftNumberLabels();
    for (int i = 0; i < 8; i++) {
      actual += labels[i].getText();
    }
    assertEquals(expected, actual);
  }

  @Test
  public void setTopNumberLabelsTest() {
    init();
    guiContainer.setTopNumberLabels();
    String expected = guiContainer.isWhitePlacement() ? "abcdefgh" : "hgfedcba";
    String actual = new String();
    guiContainer.setTopNumberLabels();
    JLabel[] labels = guiContainer.getTopNumberLabels();
    for (int i = 0; i < 8; i++) {
      actual += labels[i].getText();
    }
    assertEquals(expected, actual);
  }

  @Test
  public void setBottomNumberLabelsTest() {
    init();
    guiContainer.setBottomNumberLabels();
    String expected = guiContainer.isWhitePlacement() ? "abcdefgh" : "hgfedcba";
    String actual = new String();
    guiContainer.setBottomNumberLabels();
    JLabel[] labels = guiContainer.getBottomNumberLabels();
    for (int i = 0; i < 8; i++) {
      actual += labels[i].getText();
    }
    assertEquals(expected, actual);
  }

  @Test
  void createTopNumberLabelsTest() {
    init();
    JPanel mainPanel = new JPanel();
    int x = 0;
    int y = 0;

    guiContainer.createTopNumberLabels(mainPanel, x, y);

    Component[] components = mainPanel.getComponents();
    assertEquals(8, components.length);

    int expectedX = x + 17;
    int expectedY = y - 20;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(15, bounds.width);
      assertEquals(15, bounds.height);
      expectedX += 50;
    }
  }

  @Test
  void createBottomNumberLabelsTest() {
    init();
    JPanel mainPanel = new JPanel();
    int x = 0;
    int y = 0;

    guiContainer.createBottomNumberLabels(mainPanel, x, y);

    Component[] components = mainPanel.getComponents();
    assertEquals(8, components.length);

    int expectedX = x + 17;
    int expectedY = y + 405;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(15, bounds.width);
      assertEquals(15, bounds.height);
      expectedX += 50;
    }
  }

  @Test
  void createLeftNumberLabelsTest() {
    init();
    JPanel mainPanel = new JPanel();
    int x = 0;
    int y = 0;

    guiContainer.createLeftNumberLabels(mainPanel, x, y);

    Component[] components = mainPanel.getComponents();
    assertEquals(8, components.length);

    int expectedX = x - 15;
    int expectedY = y + 20;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(15, bounds.width);
      assertEquals(15, bounds.height);
      expectedY += 50;
    }
  }

  @Test
  void createRightNumberLabelsTest() {
    init();
    JPanel mainPanel = new JPanel();
    int x = 0;
    int y = 0;

    guiContainer.createRightNumberLabels(mainPanel, x, y);

    Component[] components = mainPanel.getComponents();
    assertEquals(8, components.length);

    int expectedX = x + 405;
    int expectedY = y + 20;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(15, bounds.width);
      assertEquals(15, bounds.height);
      expectedY += 50;
    }
  }

  @Test
  void setBlackRookTest() {
    init();
    Piece piece = new Rook(BLACK);
    guiContainer.setPieceIcon(piece, 0, 0);
    ImageIcon actualIcon = (ImageIcon) cells[0][0].getIcon();
    ImageIcon expectedIcon =
        new ImageIcon(
            Objects.requireNonNull(getClass().getClassLoader().getResource("images/BlackR.png")));
    assertEquals(expectedIcon.getImage(), actualIcon.getImage());
  }

  @Test
  void setWhiteQueenTest() {
    init();
    Piece piece = new Queen(WHITE);
    guiContainer.setPieceIcon(piece, 3, 3);
    ImageIcon actualIcon = (ImageIcon) cells[3][3].getIcon();
    ImageIcon expectedIcon =
        new ImageIcon(
            Objects.requireNonNull(getClass().getClassLoader().getResource("images/WhiteQ.png")));
    assertEquals(expectedIcon.getImage(), actualIcon.getImage());
  }

  @Test
  void removePieceIconTest() {
    init();
    int col = 3;
    int row = 4;
    Icon icon = new ImageIcon("dummyIcon.png");
    cells[row][col].setIcon(icon);
    // Проверяем, что перед вызовом метода иконка установлена
    assertNotNull(cells[row][col].getIcon());
    guiContainer.removePieceIcon(col, row);
    // Проверяем, что после вызова метода иконка удалена
    assertNull(cells[row][col].getIcon());
  }

  @Test
  void highlightCellTest() {
    init();
    int col = 2;
    int row = 5;

    // Вызываем метод подсветки клетки
    guiContainer.highlightCell(col, row);

    // Проверяем, что цвет клетки изменился на ожидаемый
    assertEquals(new Color(0, 255, 0), cells[row][col].getBackground());
  }

  @Test
  void smallHighlightCellTest() {
    init();
    int col = 6;
    int row = 2;

    // Вызываем метод слабой подсветки клетки
    guiContainer.smallHighlightCell(col, row);

    // Проверяем, что цвет клетки изменился на ожидаемый
    assertEquals(new Color(0, 50, 0), cells[row][col].getBackground());
  }
}
