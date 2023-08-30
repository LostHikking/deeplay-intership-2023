package io.deeplay.grandmastery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
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
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class GuiTest {
  private Gui gui;
  private GuiContainer guiContainer;

  private void init() {
    gui = new Gui(false);
    guiContainer = gui.getGuiContainer();
  }
  
  @Test
  public void testAnswerDrawForDecline() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showAnswerDrawWindow()).thenReturn(0);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод answerDraw() и проверяем результат
      assertFalse(gui.answerDraw());
    }
  }

  @Test
  public void testAnswerDrawForAccept() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showAnswerDrawWindow()).thenReturn(1);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод answerDraw() и проверяем результат
      assertTrue(gui.answerDraw());
    }
  }
  
  @Test
  public void testConfirmSurForYes() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showConfirmSurWindow()).thenReturn(0);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод confirmSur() и проверяем результат
      assertTrue(gui.confirmSur());
    }
  }

  @Test
  public void testConfirmSurForNo() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showConfirmSurWindow()).thenReturn(1);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод confirmSur() и проверяем результат
      assertFalse(gui.confirmSur());
    }
  }
  
  @Test
  public void testSelectColorForWhite() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showColorSelectionWindow()).thenReturn(0);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод selectColor() и проверяем результат
      assertEquals(gui.selectColor(), Color.WHITE);
    }
  }

  @Test
  public void testSelectColorForBlack() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showColorSelectionWindow()).thenReturn(1);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод selectColor() и проверяем результат
      assertEquals(gui.selectColor(), Color.BLACK);
    }
  }
  
  @Test
  public void testSelectModeForHumanVsBot() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showModeSelectionWindow()).thenReturn(0);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод selectMode() и проверяем результат
      assertEquals(gui.selectMode(), GameMode.HUMAN_VS_BOT);
    }
  }

  @Test
  public void testSelectModeForBotVsBot() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showModeSelectionWindow()).thenReturn(2);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод selectMode() и проверяем результат
      assertEquals(gui.selectMode(), GameMode.BOT_VS_BOT);
    }
  }
  
  @Test
  public void testSelectModeForHumanVsHuman() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showModeSelectionWindow()).thenReturn(1);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод selectMode() и проверяем результат
      assertEquals(gui.selectMode(), GameMode.HUMAN_VS_HUMAN);
    }
  }
  
  @Test
  public void testSelectChessTypeForClassic() throws IllegalAccessException {
    Gui gui = new Gui(false); // создаем объект Gui
    GuiContainer guiContainer = mock(GuiContainer.class); // создаем mock-объект GuiContainer
    // устанавливаем mock-объект в Gui
    FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainer, true);
    // вызываем метод selectChessType() и проверяем результат
    assertEquals(gui.selectChessType(), ChessType.CLASSIC);
  }
  
  @Test
  public void testSelectChessTypeForFishers() throws IllegalAccessException {
    try (MockedStatic<GuiContainer> mocked = Mockito.mockStatic(GuiContainer.class)) {
      Gui gui = new Gui(false); // создаем объект Gui
      GuiContainer guiContainerMock = mock(GuiContainer.class); // создаем mock-объект GuiContainer
      when(guiContainerMock.showChessTypeSelectionWindow()).thenReturn(1);
      // устанавливаем mock-объект в Gui
      FieldUtils.writeDeclaredField(gui, "guiContainer", guiContainerMock, true);
      // вызываем метод selectChessType() и проверяем результат
      assertEquals(gui.selectChessType(), ChessType.FISHERS);
    }
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
  public void testPrintEventMessage() {
    init();
    
    String testMessage = "Test message";
    gui.printEventMessage(testMessage);

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
