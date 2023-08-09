package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import java.io.IOException;

/**
 * Класс {@code GameController} управляет игровым процессом, обеспечивая взаимодействие между
 * игроками, доской и интерфейсом пользователя.
 */
public class GameController implements StateListener {
  private final GameListener gameListener;

  private final GameListener historyListener;

  private final Board board;

  private PlayerListener white;

  private PlayerListener black;

  private GameState gameStatus;

  private UI ui;

  private Move move;

  /** Дефолтный конструктор класса {@code GameController}. */
  public GameController() {
    this.move = null;
    this.white = null;
    this.black = null;
    this.ui = null;
    this.board = new HashBoard();

    Game game = new Game();
    GameHistory gameHistory = new GameHistory();
    game.setStateListener(this);
    game.setGameHistoryListener(gameHistory);

    this.gameListener = game;
    this.historyListener = gameHistory;
  }

  public void setUi(UI ui) {
    this.ui = ui;
  }

  private void notifyPlayerMakeMove(PlayerListener player) throws GameException {
    player.makeMove(board);
    this.move = player.getMoveData();
  }

  private void notifyGameStartup() {
    gameListener.startup(board);
    if (gameStatus != GameState.INIT_STATE) {
      historyListener.startup(board);
    }
  }

  private void notifyGameMakeMove() {
    gameListener.makeMove(move, board);
    if (gameStatus != GameState.IMPOSSIBLE_MOVE && gameStatus != GameState.ROLLBACK) {
      historyListener.makeMove(move, board);
    }
  }

  private void notifyGameRollBack() {
    historyListener.rollback(board);
    gameListener.rollback(board);
  }

  /**
   * Запускает игру в соответствии с выбранным режимом и конфигурацией.
   *
   * @throws GameException если возникла ошибка во время игры
   * @throws IOException если возникла ошибка ввода/вывода
   */
  public void beginPlay() throws GameException, IOException {
    switch (ui.selectMode()) {
      case BOT_VS_BOT -> {
        white = new AiPlayer(board, Color.WHITE);
        black = new AiPlayer(board, Color.BLACK);
      }
      case HUMAN_VS_BOT -> {
        Color color = ui.selectColor();
        String playerName = ui.inputPlayerName(color);

        if (color == Color.WHITE) {
          white = new HumanPlayer(playerName, board, Color.WHITE, ui);
          black = new AiPlayer(board, Color.BLACK);
        } else if (color == Color.BLACK) {
          black = new HumanPlayer(playerName, board, Color.BLACK, ui);
          white = new AiPlayer(board, Color.WHITE);
        }
      }
      case HUMAN_VS_HUMAN -> {
        white = new HumanPlayer(ui.inputPlayerName(Color.WHITE), board, Color.WHITE, ui);
        black = new HumanPlayer(ui.inputPlayerName(Color.BLACK), board, Color.BLACK, ui);
      }
      default -> throw GameErrorCode.UNKNOWN_GAME_MODE.asException();
    }

    notifyGameStartup();
    if (board.getWhiteKingPosition() == null || board.getBlackKingPosition() == null) {
      throw GameErrorCode.ERROR_START_GAME.asException();
    } else {
      ui.printHelp();
      ui.showBoard(board);
    }
  }

  /**
   * Выполняет следующий игровой ход в соответствии с текущим состоянием игры.
   *
   * @throws GameException если возникла ошибка во время игры
   * @throws IOException если возникла ошибка ввода/вывода
   */
  public void nextMove() throws GameException, IOException {
    PlayerListener currentPlayer;
    GameState prevStatus = gameStatus;
    if (this.gameStatus == GameState.WHITE_MOVE) {
      currentPlayer = white;
    } else if (this.gameStatus == GameState.BLACK_MOVE) {
      currentPlayer = black;
    } else {
      isGameOver();
      return;
    }

    while (move == null) {
      try {
        notifyPlayerMakeMove(currentPlayer);
      } catch (GameException e) {
        ui.incorrectMove();
      }
    }
    notifyGameMakeMove();

    if (gameStatus == GameState.IMPOSSIBLE_MOVE) {
      ui.moveImpossible(move);
      gameStatus = prevStatus;
    } else if (gameStatus == GameState.ROLLBACK) {
      notifyGameRollBack();
      ui.moveImpossible(move);
      gameStatus = prevStatus;
    } else {
      ui.showMove(board, currentPlayer);
    }

    this.move = null;
  }

  /**
   * Проверяет, завершилась ли игра.
   *
   * @return {@code true}, если игра завершилась, иначе {@code false}
   * @throws IOException если возникла ошибка ввода/вывода
   */
  public boolean isGameOver() throws IOException {
    switch (gameStatus) {
      case WHITE_WIN -> {
        ui.showResultGame(white);
        ui.close();
        return true;
      }
      case BLACK_WIN -> {
        ui.showResultGame(black);
        ui.close();
        return true;
      }
      case STALEMATE -> {
        ui.showResultGame(null);
        ui.close();
        return true;
      }
      default -> {
        return false;
      }
    }
  }

  @Override
  public void changeGameState(GameState gameState) {
    this.gameStatus = gameState;
  }

  public Board getBoard() {
    return board;
  }

  public PlayerListener getWhite() {
    return white;
  }

  public PlayerListener getBlack() {
    return black;
  }

  public GameState getGameStatus() {
    return gameStatus;
  }

  public Move getMove() {
    return move;
  }
}
