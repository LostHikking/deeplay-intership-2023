package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gui implements UI {
  private char promotionPiece;
  private boolean wantsDraw;
  private boolean wantsSurrender;
  private Point firstClick;

  /** Контейнер с компонентами графического интерфейса. */
  private GuiContainer guiContainer;
  /** Очередь, в которой хранятся клики по доске. */
  private BlockingQueue<Point> clickQueue = new LinkedBlockingQueue<>();

  private List<Move> moves;
  /** Переменная для отслеживания изменений в очереди(метод inputMove). */
  private final Object monitor;

  /** Метод для обновления окна. */
  public void repaint() {
    guiContainer.getFrame().repaint();
  }

  /** Конструктор для GUI. */
  public Gui() {
    wantsSurrender = false;
    firstClick = null;
    guiContainer = new GuiContainer();
    activateEndGamePanel();
    monitor = new Object();
    showGui();
    playBackgroundMusic("/sounds/backgroundMusic.wav", 0.6f);
  }

  private void playBackgroundMusic(String soundFile, float volume) {
    new Thread(
            () -> {
              try {
                URL url = getClass().getResource(soundFile);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
              } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  /**
   * Метод, обрабатывающий выбор пользователя в диалоговом окне для выбора расстановки.
   *
   * @return Режим игры, выбранный игроком.
   */
  @Override
  public ChessType selectChessType() {
    int choice = guiContainer.showChessTypeSelectionWindow();
    ChessType selectedChessType;
    switch (choice) {
      case 0:
        playSound("/sounds/clickSound.wav");
        selectedChessType = ChessType.CLASSIC;
        break;
      case 1:
        playSound("/sounds/clickSound.wav");
        selectedChessType = ChessType.FISHERS;
        break;
      default:
        selectedChessType = null;
    }
    return selectedChessType;
  }

  /**
   * Метод, обрабатывающий выбор пользователя в диалоговом окне для выбора режима.
   *
   * @return Режим игры, выбранный игроком.
   */
  @Override
  public GameMode selectMode() {
    int selectedMode = guiContainer.showModeSelectionWindow();
    GameMode mode;
    switch (selectedMode) {
      case 0 -> {
        playSound("/sounds/clickSound.wav");
        mode = GameMode.HUMAN_VS_BOT;
      }
      case 1 -> {
        playSound("/sounds/clickSound.wav");
        mode = GameMode.HUMAN_VS_HUMAN;
      }
      case 2 -> {
        playSound("/sounds/clickSound.wav");
        mode = GameMode.BOT_VS_BOT;
      }
      default -> mode = null;
    }
    return mode;
  }

  /**
   * Метод, обрабатывающий выбор пользователя в диалоговом окне для выбора цвета.
   *
   * @return Цвет.
   */
  @Override
  public Color selectColor() {
    int selectedColor = guiContainer.showColorSelectionWindow();
    Color color;
    switch (selectedColor) {
      case 0 -> {
        playSound("/sounds/clickSound.wav");
        color = Color.WHITE;
      }
      case 1 -> {
        playSound("/sounds/clickSound.wav");
        color = Color.WHITE;
      }
      default -> color = null;
    }
    ;
    return color;
  }

  /**
   * Метод, обрабатывающий выбор пользователя в диалоговом окне для подтверждения сдачи.
   *
   * @return Цвет.
   */
  @Override
  public boolean confirmSur() {
    int choice = guiContainer.showConfirmSurWindow();
    boolean result;
    switch (choice) {
      case 0 -> {
        playSound("/sounds/clickSound.wav");
        wantsSurrender = true;
        result = true;
      }
      case 1 -> {
        playSound("/sounds/clickSound.wav");
        wantsSurrender = false;
        result = false;
      }
      default -> result = false;
    }
    ;
    return result;
  }

  /**
   * Метод, обрабатывающий выбор пользователя в диалоговом окне для подтверждения ничьей.
   *
   * @return Цвет.
   */
  @Override
  public boolean answerDraw() {
    int choice = guiContainer.showAnswerDrawWindow();
    boolean result;
    switch (choice) {
      case 0 -> {
        playSound("/sounds/clickSound.wav");
        result = false;
      }
      case 1 -> {
        playSound("/sounds/clickSound.wav");
        result = true;
      }
      default -> result = false;
    }
    ;
    return result;
  }

  /**
   * Метод, обрабатывающий ввод имени пользователя в диалоговом окне.
   *
   * @param color Цвет игрока.
   * @return Имя.
   */
  @Override
  public String inputPlayerName(Color color) {
    String name = guiContainer.showInputPlayerNameWindow(color);
    playSound("/sounds/clickSound.wav");
    return name;
  }

  /** Метод, делающий наш фрейм видимым. */
  public void showGui() {
    guiContainer.showGameFrame();
  }

  /**
   * Метод вывода хода игрока в TextArea.
   *
   * @param move Ход
   */
  @Override
  public void showMove(Move move, Color color) {
    String messageBuilder =
        color.toString() + ": " + LongAlgebraicNotation.moveToString(move) + "\n";
    String lastLine = guiContainer.getLastLine();
    if (move != null && !messageBuilder.equals(lastLine)) {
      guiContainer.printMessage(messageBuilder);
    }
  }

  /**
   * Метод для вывода результата игры.
   *
   * @param gameState Состояние игры.
   */
  @Override
  public void showResultGame(GameState gameState) {
    String message;
    String sound;
    if (gameState == GameState.WHITE_WIN) {
      sound = "/sounds/whiteWinSound.wav";
      message = "Белые выиграли!!!";
    } else if (gameState == GameState.BLACK_WIN) {
      message = "Чёрные выиграли!!!";
      sound = "/sounds/blackWinSound.wav";
    } else {
      message = "Ничья!";
      sound = "/sounds/drawSound.wav";
    }
    playSound(sound);
    JOptionPane.showMessageDialog(null, message, "Результат игры", JOptionPane.INFORMATION_MESSAGE);
  }

  /** Метод для вывода справки. */
  @Override
  public void printHelp() {}

  /**
   * Метод отвечающий за ввод хода игрока, ждет, пока очередь из кликов заполнится.
   *
   * @param playerName Имя игрока.
   * @return Строка хода.
   */
  @Override
  public String inputMove(String playerName) {
    clickQueue.clear();
    String move = null;
    StringBuilder stringBuilder = new StringBuilder();
    try {
      // Ждем, пока размер очереди не станет равным 2
      synchronized (monitor) {
        while (!wantsSurrender && !wantsDraw && clickQueue.size() < 2) {
          monitor.wait();
        }
      }
      if (wantsDraw) {
        wantsDraw = false;
        return "draw";
      }
      if (wantsSurrender) {
        wantsSurrender = false;
        return "sur";
      }
      Point firstClick = clickQueue.take(); // Извлекаем первый клик
      Point secondClick = clickQueue.take(); // Извлекаем второй клик
      stringBuilder.append(new Column(firstClick.x).getChar());
      stringBuilder.append(new Row(firstClick.y).getChar());
      stringBuilder.append(new Column(secondClick.x).getChar());
      stringBuilder.append(new Row(secondClick.y).getChar());
      if (promotionPiece != '\0') {
        stringBuilder.append(promotionPiece);
        promotionPiece = '\0';
      }
      move = stringBuilder.toString();
    } catch (InterruptedException e) {
      System.err.println("Thread was interrupted");
      Thread.currentThread().interrupt();
    } finally {
      clickQueue.clear();
    }

    return move;
  }

  public void rotateCells() {
    guiContainer.rotateArray();
  }

  /**
   * Метод, перерисовывающий доску и устанавливающий кликабельность нужных кнопок.
   *
   * @param board Доска.
   * @param color Цвет.
   */
  @Override
  public void showBoard(Board board, Color color) {
    if (color == Color.WHITE && !guiContainer.isWhitePlacement()) {
      rotateCells();
    }
    if (color == Color.BLACK && !guiContainer.isBlackPlacement()) {
      rotateCells();
    }
    Piece piece;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        piece = board.getPiece(j, i);
        JButton cell = guiContainer.getCells()[i][j];
        if ((i + j) % 2 == 0) {
          cell.setBackground(guiContainer.getOriginalBlackColor());
        } else {
          cell.setBackground(guiContainer.getOriginalWhiteColor());
        }
        if (piece != null) {
          cell.setName(piece.getColor().toString() + piece.getFigureType().getSymbol());
          guiContainer.setPieceIcon(piece, j, i);
          if (piece.getColor() == color) {
            makeClickable(cell, j, i, board);
          } else {
            makeUnclickable(cell);
          }
        } else {
          guiContainer.removePieceIcon(j, i);
          makeUnclickable(cell);
        }
      }
    }
    repaint();
  }

  /** Метод для вывода сообщения о некорректном ходе в textArea. */
  @Override
  public void incorrectMove() {
    guiContainer.printMessage("Введён некорректный ход.\n");
  }

  /**
   * Метод устанавливающий имя ходящего пользователя в игре.
   *
   * @param playerName Имя игрока
   */
  public void setMovingPlayer(String playerName) {
    guiContainer.setLabelMessage("Ход:" + playerName + "...");
  }

  /** Убирает листенеры со всех кнопок. */
  public void makeBoardUnclickable() {
    JButton cell;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        cell = guiContainer.getCell(j, i);
        makeUnclickable(cell);
      }
    }
  }

  @Override
  public void close() {}

  /**
   * Метод, делающий возможные ходы кликабельными.
   *
   * @param board Доска для getAllMoves
   * @param col Столбец
   * @param row Строка
   */
  public void makePossibleMovesClickable(Board board, int col, int row) {
    Piece piece = board.getPiece(col, row);
    moves = piece.getAllMoves(board, new Position(new Column(col), new Row(row)));
    JButton cell = guiContainer.getCell(col, row);
    makeClickable(cell, col, row, board);
    guiContainer.smallHighlightCell(col, row);
    for (Move move : moves) {
      cell = guiContainer.getCells()[move.to().row().value()][move.to().col().value()];
      makeClickable(cell, move.to().col().value(), move.to().row().value(), board);
      guiContainer.highlightCell(move.to().col().value(), move.to().row().value());
    }
  }

  /** Метод, активирующий кнопки для сдачи и ничьей. */
  void activateEndGamePanel() {
    JButton surrenderButton = guiContainer.getSurrenderButton();
    JButton offerDrawButton = guiContainer.getOfferDrawButton();
    surrenderButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            playSound("/sounds/clickSound.wav");
            wantsSurrender = true;
            synchronized (monitor) {
              monitor.notify();
            }
          }
        });
    offerDrawButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            playSound("/sounds/clickSound.wav");
            wantsDraw = true;
            synchronized (monitor) {
              monitor.notify();
            }
          }
        });
  }

  private void playSound(String soundFile) {
    new Thread(
            () -> {
              try {
                URL soundUrl = getClass().getResource(soundFile);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
              } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  /** Метод, делающий кнопку доски кликабельной, а также устанавливающий логику нажатий по доске. */
  private void makeClickable(JButton button, int col, int row, Board board) {
    makeUnclickable(button);
    button.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
              try {
                playSound("/sounds/clickSound.wav");
                Point point = new Point(col, row);
                // Помещаем координаты в очередь
                clickQueue.put(point);
                // Если это первый клик, показываем возможные ходы
                if (clickQueue.size() == 1) {
                  makeBoardUnclickable();
                  makePossibleMovesClickable(board, col, row);
                  // System.out.println("This cell clickable");
                  synchronized (monitor) {
                    // Уведомляем метод inputMove о новом значении в очереди
                    monitor.notify();
                  }
                  // Записываем значение для проверки в следующем ифе
                  firstClick = point;
                  // Если это второй клик, скрываем возможные ходы
                } else if (clickQueue.size() == 2) {
                  playSound("/sounds/clickSound.wav");
                  // Проверка для подмены пешки
                  Piece piece = board.getPiece(firstClick.x, firstClick.y);
                  if (piece.getFigureType().getSymbol() == 'p') {
                    int row = piece.getColor() == Color.WHITE ? 6 : 1;
                    int dir = piece.getColor() == Color.WHITE ? 1 : -1;
                    if (firstClick.y == row && point.y == row + dir) {
                      pawnPromotion(piece.getColor());
                    }
                  }
                  // Сравниваем первый и второй клик, если они равны, то мы грубо говоря убираем
                  // подсветку
                  if (firstClick != null && firstClick.equals(point)) {
                    showBoard(board, board.getPiece(col, row).getColor());
                    clickQueue.clear();
                    firstClick = null;
                  } else {
                    synchronized (monitor) {
                      // Уведомляем метод inputMove о новом значении в очереди
                      monitor.notify();
                    }
                  }
                }

              } catch (InterruptedException ex) {
                System.err.println("Thread was interrupted");
                Thread.currentThread().interrupt();
              }
            }
          }
        });
  }

  /**
   * Метод выводящий диалоговое окно для выбора возрождаемой фигуры.
   *
   * @param color цвет фигуры
   */
  public void pawnPromotion(Color color) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Превращение пешки");
    dialog.setLayout(new GridLayout(1, 4));

    String colorPrefix = color == Color.WHITE ? "/images/White" : "/images/Black";

    URL queenUrl = getClass().getResource(colorPrefix + "Q.png");
    if (queenUrl != null) {
      JButton queenButton = new JButton(new ImageIcon(queenUrl));
      queenButton.setPreferredSize(new Dimension(50, 50));
      queenButton.addActionListener(
          e -> {
            playSound("/sounds/clickSound.wav");
            promotionPiece = 'q';
            dialog.dispose();
          });
      dialog.add(queenButton);
    }

    URL rookUrl = getClass().getResource(colorPrefix + "R.png");
    if (rookUrl != null) {
      JButton rookButton = new JButton(new ImageIcon(rookUrl));
      rookButton.setPreferredSize(new Dimension(50, 50));
      rookButton.addActionListener(
          e -> {
            playSound("/sounds/clickSound.wav");
            promotionPiece = 'r';
            dialog.dispose();
          });
      dialog.add(rookButton);
    }

    URL bishopUrl = getClass().getResource(colorPrefix + "B.png");
    if (bishopUrl != null) {
      JButton bishopButton = new JButton(new ImageIcon(bishopUrl));
      bishopButton.setPreferredSize(new Dimension(50, 50));
      bishopButton.addActionListener(
          e -> {
            playSound("/sounds/clickSound.wav");
            promotionPiece = 'b';
            dialog.dispose();
          });
      dialog.add(bishopButton);
    }

    URL knightUrl = getClass().getResource(colorPrefix + "N.png");
    if (knightUrl != null) {
      JButton knightButton = new JButton(new ImageIcon(knightUrl));
      knightButton.setPreferredSize(new Dimension(50, 50));
      knightButton.addActionListener(
          e -> {
            playSound("/sounds/clickSound.wav");
            promotionPiece = 'n';
            dialog.dispose();
          });
      dialog.add(knightButton);
    }

    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setModal(true);
    dialog.setResizable(false);
    dialog.setVisible(true);
  }

  /**
   * Делаем кнопку некликабельной, удаляя с неё листенеры.
   *
   * @param button Кнопка
   */
  public void makeUnclickable(JButton button) {
    // Удаляем все существующие MouseListener'ы из кнопки
    for (MouseListener listener : button.getMouseListeners()) {
      button.removeMouseListener(listener);
    }
  }
}
