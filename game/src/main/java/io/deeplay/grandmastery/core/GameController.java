package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.Boards;
import java.io.IOException;

/**
 * Класс {@code GameController} управляет игровым процессом, обеспечивая взаимодействие между
 * игроками, доской и интерфейсом пользователя.
 */
public class GameController implements StateListener {
  private final GameListener gameListener;

  private final GameListener historyListener;

  private final Board board;

  private final Player white;

  private final Player black;

  private GameState gameStatus;

  private Move move;

  /** Дефолтный конструктор класса {@code GameController}. */
  public GameController(Player playerWhite, Player playerBlack, ChessType chessType) {
    // Вот и не знаешь в каком порядке playerWhite и playerBlack выставить, чтобы ни в чём не
    // обвинили...
    this.move = null;

    if (playerWhite.getColor() == Color.WHITE) {
      this.white = playerWhite;
      this.black = playerBlack;
    } else {
      this.white = playerBlack;
      this.black = playerWhite;
    }

    this.board = new HashBoard();
    if (chessType == ChessType.CLASSIC) {
      Boards.defaultChess().accept(board);
    } else {
      Boards.fischerChess().accept(board);
    }

    Game game = new Game();
    GameHistory gameHistory = new GameHistory();
    game.setStateListener(this);
    game.setGameHistoryListener(gameHistory);

    this.gameListener = game;
    this.historyListener = gameHistory;
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
    notifyGameStartup();
    if (board.getWhiteKingPosition() == null || board.getBlackKingPosition() == null) {
      throw GameErrorCode.ERROR_START_GAME.asException();
    } else {
      white.getUi().printHelp();
      white.getUi().showBoard(board);
      black.getUi().printHelp();
      black.getUi().showBoard(board);
    }
  }

  /**
   * Выполняет следующий игровой ход в соответствии с текущим состоянием игры.
   *
   * @throws GameException если возникла ошибка во время игры
   */
  public void nextMove() throws GameException {
    Player currentPlayer;
    Player otherPlayer;
    GameState prevStatus = gameStatus;
    if (this.gameStatus == GameState.WHITE_MOVE) {
      currentPlayer = white;
      otherPlayer = black;
    } else if (this.gameStatus == GameState.BLACK_MOVE) {
      currentPlayer = black;
      otherPlayer = white;
    } else {
      isGameOver();
      return;
    }

    while (move == null) {
      try {
        notifyPlayerMakeMove(currentPlayer);
      } catch (GameException e) {
        currentPlayer.getUi().incorrectMove();
      }
    }
    notifyGameMakeMove();

    if (gameStatus == GameState.IMPOSSIBLE_MOVE) {
      currentPlayer.getUi().moveImpossible(move);
      gameStatus = prevStatus;
    } else if (gameStatus == GameState.ROLLBACK) {
      notifyGameRollBack();
      currentPlayer.getUi().moveImpossible(move);
      gameStatus = prevStatus;
    } else {
      otherPlayer.getUi().showMove(board, currentPlayer);
    }

    this.move = null;
  }

  /**
   * Проверяет, завершилась ли игра.
   *
   * @return {@code true}, если игра завершилась, иначе {@code false}
   */
  public boolean isGameOver() {
    switch (gameStatus) {
      case WHITE_WIN -> {
        white.getUi().showResultGame(white);
        white.getUi().close();
        black.getUi().showResultGame(white);
        black.getUi().close();

        return true;
      }
      case BLACK_WIN -> {
        white.getUi().showResultGame(black);
        white.getUi().close();
        black.getUi().showResultGame(black);
        black.getUi().close();
        return true;
      }
      case STALEMATE -> {
        white.getUi().showResultGame(null);
        white.getUi().close();
        black.getUi().showResultGame(null);
        black.getUi().close();
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
