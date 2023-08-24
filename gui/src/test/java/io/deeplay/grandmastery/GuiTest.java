package io.deeplay.grandmastery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.HumanPlayer;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.listeners.InputListener;
import io.deeplay.grandmastery.utils.Boards;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.BlockingQueue;
import javax.swing.JButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuiTest {
  private Gui gui;
  private GuiContainer guiContainer;

  private void init() {
    gui = new Gui();
    guiContainer = gui.getGuiContainer();
  }

  @Test
  void showMove() {
    init();
    String expected = "Moto: a2a4\n";
    guiContainer.printMessage(expected);
    InputListener inputListener = mock(InputListener.class);
    Color color = mock(Color.class);
    Player player = new HumanPlayer("Moto", color, inputListener);
    player.setLastMove("a2a4");
    gui.showMove(player);
    String actual = guiContainer.getTextArea().getText();
    assertEquals(expected, actual);
  }

  @Test
  void setMovingPlayer() {
    init();
    String expected = "Ход:Moto...";
    gui.setMovingPlayer("Moto");
    String actual = guiContainer.getMovingPlayerLabel().getText();
    assertEquals(expected, actual);
  }

  @Test
  void makeUnclickableTest() {
    init();
    JButton button = new JButton();
    button.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
          }
        });
    gui.makeUnclickable(button);
    assertEquals(0, button.getMouseListeners().length);
  }

  @Test
  void makeBoardUnclickable() {
    init();
    gui.makeBoardUnclickable();
    JButton cell;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        cell = guiContainer.getCell(j, i);
        assertEquals(0, cell.getMouseListeners().length);
      }
    }
  }

  @Test
  void activateEndGamePanelTest() {
    init();
    JButton surrenderButton = guiContainer.getSurrenderButton();
    JButton offerDrawButton = guiContainer.getOfferDrawButton();
    gui.activateEndGamePanel();
    assertTrue(surrenderButton.getActionListeners().length > 0);
    assertTrue(offerDrawButton.getActionListeners().length > 0);
  }

  @Test
  void makePossibleMovesClickableTest() {
    init();
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    gui.makeBoardUnclickable();
    gui.makePossibleMovesClickable(board, 1, 0);
    JButton cell;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (i == 2 && j == 0) {
          continue;
        }
        if (i == 2 && j == 2) {
          continue;
        }
        if (i == 0 && j == 1) {
          continue;
        }
        cell = guiContainer.getCell(j, i);
        assertEquals(0, cell.getMouseListeners().length);
      }
    }
    assertTrue(guiContainer.getCell(0, 2).getMouseListeners().length > 0);
    assertTrue(guiContainer.getCell(2, 2).getMouseListeners().length > 0);
  }

  @Test
  void inputSimpleMoveTest() throws InterruptedException {
    init();
    String expected = "a2a4";
    BlockingQueue<Point> clickQueue = gui.getClickQueue();
    clickQueue.put(new Point(0, 1));
    clickQueue.put(new Point(0, 3));
    String actual = gui.inputMove("Moto");
    assertEquals(expected, actual);
  }

  @Test
  void inputSurrenderMoveTest() {
    init();
    gui.setWantsSurrender(true);
    String expected = "sur";
    String actual = gui.inputMove("Player");
    assertEquals(expected, actual);
  }

  @Test
  void inputDrawMoveTest() {
    init();
    gui.setWantsDraw(true);
    String expected = "draw";
    String actual = gui.inputMove("Player");
    assertEquals(expected, actual);
  }

  @Test
  void showBlackBoardTest() {
    init();
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    gui.showBoard(board, Color.BLACK);
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (i <= 5) {
          assertTrue(guiContainer.getCell(j, i).getMouseListeners().length == 0);
        } else {
          assertTrue(guiContainer.getCell(j, i).getMouseListeners().length > 0);
          String cellName = guiContainer.getCell(j, i).getName();
          assertTrue(cellName.contains("BLACK"));
        }
      }
    }
  }

  @Test
  void showWhiteBoardTest() {
    init();
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    gui.showBoard(board, Color.WHITE);
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (i <= 1) {
          assertTrue(guiContainer.getCell(j, i).getMouseListeners().length > 0);
          String cellName = guiContainer.getCell(j, i).getName();
          assertTrue(cellName.contains("WHITE"));
        } else {
          assertTrue(guiContainer.getCell(j, i).getMouseListeners().length == 0);
        }
      }
    }
  }
}
