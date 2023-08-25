package io.deeplay.grandmastery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.Boards;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GuiTest {
  private Gui gui;
  private GuiContainer guiContainer;

  private void init() {
    gui = new Gui(false);
    guiContainer = gui.getGuiContainer();
  }
  
  @Test
  public void testPlaySoundUnsupportedFile() {
    init();

    String incorrectSoundFile = "file.txt";
    int volumeIndex = 1;
    assertThrows(NullPointerException.class,
            () -> gui.playBackgroundMusic(incorrectSoundFile, volumeIndex));
  
  }

  @Test
  void activateVolumePanel() {
    init();
    GuiContainer mockGuiContainer = mock(GuiContainer.class);
    JPanel mockVolumePanel = mock(JPanel.class);
    JButton mockVolumeButton = mock(JButton.class);
    when(mockGuiContainer.getVolumePanel()).thenReturn(mockVolumePanel);
    when(mockVolumePanel.getComponent(1)).thenReturn(mockVolumeButton);
    gui.setGuiContainer(mockGuiContainer);
    gui.activateVolumePanel();
    verify(mockGuiContainer).getVolumePanel();
    verify(mockVolumePanel).getComponent(1);
    verify(mockVolumeButton).setIcon(any());
    verify(mockVolumeButton).addActionListener(any());
  }  
  
  @Test
  public void testAddLog() {
    init();
    
    String testMessage = "Test message";
    gui.addLog(testMessage);

    String actualContent = guiContainer.getLogTextArea().getText();

    assertEquals(testMessage, actualContent);
  }
  
  @Test
  public void testActivateExitButton() {
    init();
    // Вызовите метод для активации кнопки выхода
    gui.activateExitButton();

    // Получите JFrame для тестирования
    JFrame frame = guiContainer.getFrame();

    // Проверьте, что листенер установлен и совпадает с нашим типом листенера
    WindowListener foundListener = null;
    for (WindowListener listener : frame.getWindowListeners()) {
      if (listener instanceof WindowAdapter) {
        foundListener = listener;
        break;
      }
    }
    assertNotNull(foundListener, "WindowAdapter listener должен быть установлен");
  }

  @Test
  void testPlayBackgroundMusicThrowsNullPointerException() {
    String incorrectSoundFile = "non_existent_sound.wav";

    assertThrows(NullPointerException.class, () -> gui.playBackgroundMusic(incorrectSoundFile, 1));
  }

  @Test
  void testPlaySoundThrowsNullPointerException() {
    String incorrectSoundFile = "non_existent_sound.wav";
    assertThrows(NullPointerException.class, () -> gui.playSound(incorrectSoundFile, 1));
  }

  @Test
  void showMove() {
    init();
    String expected = "BLACK: a2a4\n";
    guiContainer.printMessage(expected);
    Column colFrom = new Column(0);
    Column colTo = new Column(0);
    Row rowFrom = new Row(1);
    Row rowTo = new Row(3);
    Move move = new Move(new Position(colFrom, rowFrom), new Position(colTo, rowTo), null);
    gui.showMove(move, Color.BLACK);
    String actual = guiContainer.getTextArea().getText();
    assertEquals(expected, actual);
  }

  @Test
  void setMovingPlayer() {
    init();
    String expected = "Ход белых...";
    gui.setMovingPlayer("бел");
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
  @Disabled
  void inputSimpleMoveTest() throws InterruptedException, ExecutionException {
    init();

    var future = new FutureTask<>(() -> gui.inputMove("Moto"));
    new Thread(future).start();

    gui.getClickQueue().put(new Point(0, 1));
    gui.getClickQueue().put(new Point(0, 3));

    synchronized (gui.getMonitor()) {
      gui.getMonitor().notify();
    }

    assertEquals("a2a4", future.get());
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
