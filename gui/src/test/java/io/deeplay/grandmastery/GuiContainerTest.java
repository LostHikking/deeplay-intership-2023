package io.deeplay.grandmastery;

import static io.deeplay.grandmastery.domain.Color.BLACK;
import static io.deeplay.grandmastery.domain.Color.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;


class GuiContainerTest {

  private JButton[][] cells;

  private GuiContainer guiContainer;

  public void init() {
    guiContainer = new GuiContainer();
    cells = guiContainer.getCells();
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

    int expectedX = x + 20;
    int expectedY = y - 20;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(10, bounds.width);
      assertEquals(10, bounds.height);
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

    int expectedX = x + 20;
    int expectedY = y + 410;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(10, bounds.width);
      assertEquals(10, bounds.height);
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
      assertEquals(10, bounds.width);
      assertEquals(10, bounds.height);
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

    int expectedX = x + 410;
    int expectedY = y + 20;
    for (int i = 0; i < 8; i++) {
      assertTrue(components[i] instanceof JLabel);
      JLabel label = (JLabel) components[i];
      Rectangle bounds = label.getBounds();
      assertEquals(expectedX, bounds.x);
      assertEquals(expectedY, bounds.y);
      assertEquals(10, bounds.width);
      assertEquals(10, bounds.height);
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
            Objects.requireNonNull(getClass().getClassLoader().getResource("BlackR.png")));
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
            Objects.requireNonNull(getClass().getClassLoader().getResource("WhiteQ.png")));
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
