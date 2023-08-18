package io.deeplay.grandmastery;

import java.awt.*;
import java.util.List;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.core.PlayerInfo;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Boards;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.IOException;

public class GameGui implements UI {
  private char promotionPiece;
  private Point firstClick = null;

  /** Контейнер с компонентами графического интерфейса. */
  private GameGuiContainer gameGuiContainer;
  /** Очередь, в которой хранятся клики по доске. */
  private BlockingQueue<Point> clickQueue = new LinkedBlockingQueue<>();

  private List<Move> moves;
  /** Переменная для отслеживания изменений в очереди(метод inputMove). */
  private final Object monitor;

  /** Метод для обновления окна. */
  public void repaint() {
    gameGuiContainer.getFrame().repaint();
  }

  /** Конструктор для GUI */
  public GameGui() {
    gameGuiContainer = new GameGuiContainer();
    monitor = new Object();
  }
  /**
   * Метод, открывающий диалоговое окно для выбора начальной расстановки.
   *
   * @return Режим игры, выбранный игроком.
   */
  @Override
  public ChessType selectChessType() {
    Object[] options = {"Классические", "Фишера"};

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel messageLabel = new JLabel("Выберите начальную расстановку:");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    messageLabel.setOpaque(true);

    panel.add(Box.createVerticalGlue());
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue());

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

    return switch (choice) {
      case 0 -> ChessType.CLASSIC;
      case 1 -> ChessType.FISHERS;
      default -> null;
    };
  }

  /**
   * Метод открывает небольшое меню для выбора режима игры
   *
   * @return Режим игры, выбранный игроком.
   */
  @Override
  public GameMode selectMode() {
    Object[] options = {"Human vs Bot", "Human vs Human", "Bot vs Bot"};

    // Создаем панель для выравнивания компонентов
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Создаем компонент с выравниванием по центру
    JLabel messageLabel = new JLabel("Выберите режим игры:");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Добавляем компоненты на панель
    panel.add(Box.createVerticalGlue()); // Пространство сверху
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue()); // Пространство снизу

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

    return switch (choice) {
      case 0 -> GameMode.HUMAN_VS_BOT;
      case 1 -> GameMode.HUMAN_VS_HUMAN;
      case 2 -> GameMode.BOT_VS_BOT;
      default -> null;
    };
  }
  /**
   * Метод, открывающий небольшое диалоговое окно для выбора цвета.
   *
   * @return Цвет.
   */
  @Override
  public Color selectColor() {
    Object[] options = {"Белый", "Черный"};
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    JLabel messageLabel = new JLabel("Выберите цвет:");
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    messageLabel.setOpaque(true);
    panel.add(Box.createVerticalGlue());
    panel.add(messageLabel);
    panel.add(Box.createVerticalGlue());
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

    return switch (choice) {
      case 0 -> Color.WHITE;
      case 1 -> Color.BLACK;
      default -> null;
    };
  }
  /**
   * Метод для ввода имени игрока через диалоговое окно.
   *
   * @param color Цвет игрока.
   * @return Имя.
   */
  @Override
  public String inputPlayerName(Color color) {
    String message = (color == Color.WHITE ? "Белый" : "Черный") + " игрок, введите ваше имя:";
    JTextField textField = new JTextField(20);
    textField.setAlignmentX(Component.CENTER_ALIGNMENT);
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(Box.createVerticalGlue());
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
  /** Метод, делающий наш фрейм видимым. */
  public void showGui() {
    gameGuiContainer.showGameFrame();
  }
  /**
   * Метод вывода хода игрока в TextArea.
   *
   * @param board Доска
   * @param movePlayer Игрок, совершающий ход.
   */
  @Override
  public void showMove(Board board, PlayerInfo movePlayer) {
 showBoard(board, movePlayer.getColor() == Color.WHITE ? Color.WHITE : Color.BLACK);
  }
  /**
   * Метод для вывода результата игры.
   *
   * @param gameState Состояние игры.
   */
  @Override
  public void showResultGame(GameState gameState) {
    String message;
    if (gameState == GameState.WHITE_WIN) {
      message = "Белые выиграли!!!";
    } else if (gameState == GameState.BLACK_WIN) {
      message = "Чёрные выиграли!!!";
    } else {
      message = "Ничья!";
    }
    JOptionPane.showMessageDialog(null, message, "Результат игры", JOptionPane.INFORMATION_MESSAGE);
  }
  /**
   * Метод отвечающий за ввод хода игрока, ждет, пока очередь из кликов заполнится.
   *
   * @param playerName Имя игрока.
   * @return Строка хода.
   */
  @Override
  public String inputMove(String playerName){
    String move = null;
    StringBuilder stringBuilder = new StringBuilder();
    try {
      // Ждем, пока размер очереди не станет равным 2
      synchronized (monitor) {
        while (clickQueue.size() < 2) {
          monitor.wait();
        }
      }
      Point firstClick = clickQueue.take(); // Извлекаем первый клик
      Point secondClick = clickQueue.take(); // Извлекаем второй клик
      stringBuilder.append(new Column(firstClick.x).getChar());
      stringBuilder.append(new Row(firstClick.y).getChar());
      stringBuilder.append(new Column(secondClick.x).getChar());
      stringBuilder.append(new Row(secondClick.y).getChar());
      if(promotionPiece!='\0'){
        stringBuilder.append(promotionPiece);
        promotionPiece='\0';
      }
      move = stringBuilder.toString();
    } catch (InterruptedException e) {
      System.err.println("Thread was interrupted");
      Thread.currentThread().interrupt();
    } finally {
      clickQueue.clear();
    }
    String messageBuilder = playerName + ": " + move + "\n";
    gameGuiContainer.printMessage(messageBuilder);
    setMovingPlayer(playerName);
    return move;
  }
  /**
   * Метод, перерисовывающий доску и устанавливающий кликабельность нужных кнопок
   *
   * @param board Доска.
   * @param color Цвет.
   */
  @Override
  public void showBoard(Board board, Color color) {
    Piece piece;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {

        piece = board.getPiece(j, i);

        JButton cell = gameGuiContainer.getCells()[i][j];
        if ((i + j) % 2 == 0) {
          cell.setBackground(gameGuiContainer.getOriginalBlackColor());
        } else {
          cell.setBackground(gameGuiContainer.getOriginalWhiteColor());
        }
        if (piece != null) {
          gameGuiContainer.setPieceIcon(piece, j, i);
          if (piece.getColor() == color) {
            makeClickable(cell, j, i, board);
          } else {
            makeUnclickable(cell);
          }
        } else {
          gameGuiContainer.removePieceIcon(j, i);
          makeUnclickable(cell);
        }
      }
    }
    repaint();
  }
  /** Метод для вывода сообщения о некорректном ходе в textArea */
  @Override
  public void incorrectMove() {
    gameGuiContainer.printMessage("Введён некорректный ход.\n");
  }

  public void setMovingPlayer(String playerName) {
    gameGuiContainer.setLabelMessage("Ход:" + playerName + "...");
  }

  /** Убирает листенеры со всех кнопок. */
  public void makeBoardUnclickable() {
    JButton cell;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        cell = gameGuiContainer.getCell(j, i);
        makeUnclickable(cell);
      }
    }
  }

  @Override
  public void printHelp() throws IOException {}

  @Override
  public void emptyStartPosition(Move move) {
    gameGuiContainer.printMessage("Стартовая позиция пуста.\n");
  }

  @Override
  public void moveImpossible(Move move) {
    gameGuiContainer.printMessage("Введён невозможный ход.\n");
  }

  @Override
  public void warningYourKingInCheck(Move move) {}

  @Override
  public void close() {}

  /**
   * Метод, делающий возможные ходы кликабельными
   *
   * @param board Доска для getAllMoves
   * @param col Столбец
   * @param row Строка
   */
  public void makePossibleMovesClickable(Board board, int col, int row) {
    Piece piece = board.getPiece(col, row);
    moves = piece.getAllMoves(board, new Position(new Column(col), new Row(row)));
    JButton cell = gameGuiContainer.getCell(col, row);
    makeClickable(cell, col, row, board);
    gameGuiContainer.smallHighlightCell(col, row);
    for (Move move : moves) {
      cell = gameGuiContainer.getCells()[move.to().row().value()][move.to().col().value()];
      makeClickable(cell, move.to().col().value(), move.to().row().value(), board);
      gameGuiContainer.highlightCell(move.to().col().value(), move.to().row().value());
    }
  }

  private void makeClickable(JButton button, int col, int row, Board board) {
    makeUnclickable(button);
    button.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
              try {
                Point point = new Point(col,row);
                // Помещаем координаты в очередь
                clickQueue.put(point);
                // Если это первый клик, показываем возможные ходы
                if (clickQueue.size() == 1) {
                  makeBoardUnclickable();
                  makePossibleMovesClickable(board, col, row);
                  //System.out.println("This cell clickable");
                  synchronized (monitor) {
                    // Уведомляем метод inputMove о новом значении в очереди
                    monitor.notify();
                  }
                  // Записываем значение для проверки в следующем ифе
                  firstClick = new Point(col, row);
                }
                // Если это второй клик, скрываем возможные ходы
                else if (clickQueue.size() == 2) {
                  //Проверка для подмены пешки
                  Piece piece = board.getPiece(firstClick.x, firstClick.y);
                  if(piece.getFigureType().getSymbol()=='p'){
                    int row = piece.getColor() == Color.WHITE? 6:1;
                    int dir = piece.getColor() == Color.WHITE? 1:-1;
                    if(firstClick.y==row&&point.y==row+dir){
                      pawnPromotion(piece.getColor());
                    }
                  }
                  // Сравниваем первый и второй клик, если они равны, то мы грубо говоря убираем подсветку
                  if (firstClick != null && firstClick.equals(new Point(col, row))) {
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
  public void pawnPromotion(Color color) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Превращение пешки");
    dialog.setLayout(new GridLayout(1, 4));

    String colorPrefix = color == Color.WHITE ? "White" : "Black";

    JButton queenButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource(colorPrefix + "Q.png")));
    queenButton.setPreferredSize(new Dimension(50, 50));
    queenButton.addActionListener(e -> {
      promotionPiece = 'q';
      dialog.dispose();
    });
    dialog.add(queenButton);

    JButton rookButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource(colorPrefix + "R.png")));
    rookButton.setPreferredSize(new Dimension(50, 50));
    rookButton.addActionListener(e -> {
      promotionPiece = 'r';
      dialog.dispose();
    });
    dialog.add(rookButton);

    JButton bishopButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource(colorPrefix + "B.png")));
    bishopButton.setPreferredSize(new Dimension(50, 50));
    bishopButton.addActionListener(e -> {
      promotionPiece = 'b';
      dialog.dispose();
    });
    dialog.add(bishopButton);

    JButton knightButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource(colorPrefix + "N.png")));
    knightButton.setPreferredSize(new Dimension(50, 50));
    knightButton.addActionListener(e -> {
      promotionPiece = 'n';
      dialog.dispose();
    });
    dialog.add(knightButton);

    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setModal(true);
    dialog.setResizable(false);
    dialog.setVisible(true);
  }



  /**
   * Делаем кнопку некликабельной, удаляя с неё листенеры
   *
   * @param button
   */
  private void makeUnclickable(JButton button) {
    // Удаляем все существующие MouseListener'ы из кнопки
    for (MouseListener listener : button.getMouseListeners()) {
      button.removeMouseListener(listener);
    }
  }

  public static void main(String[] args) {
    Board board = new HashBoard();
    Boards.defaultChess().accept(board);
    GameGui gui = new GameGui();
    gui.showBoard(board, Color.WHITE);
  }
}
