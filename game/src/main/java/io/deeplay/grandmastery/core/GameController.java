package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Класс {@code GameController} управляет игровым процессом, обеспечивая взаимодействие между
 * игроками, доской и интерфейсом пользователя.
 */
public class GameController {
  private final GameListener gameListener;

  private final List<GameListener> historyAndPlayer;

  private final Player white;

  private final Player black;

  @Getter
  private final GameHistory gameHistory;

  private final Game game;

  @Getter
  private GameState gameStatus;

  private Player currentPlayer;

  @Getter
  private PlayerInfo winPlayer;

  @Getter
  private Move move;

  /** Дефолтный конструктор класса {@code GameController}. */
  public GameController(Player firstPlayer, Player secondPlayer) {
    this.move = null;
    this.historyAndPlayer = new ArrayList<>();

    if (firstPlayer.getColor() == Color.WHITE) {
      this.white = firstPlayer;
      this.black = secondPlayer;
    } else {
      this.white = secondPlayer;
      this.black = firstPlayer;
    }

    this.game = new Game();
    this.gameHistory = new GameHistory();
    this.gameListener = game;
    this.historyAndPlayer.add(gameHistory);
    this.historyAndPlayer.add(white);
    this.historyAndPlayer.add(black);
  }

  private void notifyGameStartup(Board board) {
    gameListener.startup(board);
    for (GameListener listener : historyAndPlayer) {
      listener.startup(board);
    }
  }

  private void notifyGameMakeMove() throws GameException {
    gameListener.makeMove(move);
    gameHistory.addBoard(game.getCopyBoard());
    for (GameListener listener : historyAndPlayer) {
      listener.makeMove(move);
    }
  }

  private void notifyGameOver() throws GameException {
    gameListener.gameOver();
    for (GameListener listener : historyAndPlayer) {
      listener.gameOver();
    }
  }

  /**
   * Запускает игру в соответствии с выбранным режимом и конфигурацией.
   *
   * @throws GameException если возникла ошибка во время игры
   */
  public void beginPlay(ChessType chessType) throws GameException {
    Board board = new HashBoard();
    if (chessType == ChessType.CLASSIC) {
      Boards.defaultChess().accept(board);
    } else {
      Boards.fischerChess().accept(board);
    }

    if (board.getWhiteKingPosition() == null || board.getBlackKingPosition() == null) {
      throw GameErrorCode.ERROR_START_GAME.asException();
    }
    notifyGameStartup(board);
    gameStatus = GameState.WHITE_MOVE;
    this.currentPlayer = white;
  }

  /**
   * Выполняет следующий игровой ход в соответствии с текущим состоянием игры.
   *
   * @throws GameException если возникла ошибка во время игры
   */
  public void nextMove() throws GameException {
    if (this.gameStatus == GameState.WHITE_MOVE) {
      currentPlayer = white;
    } else if (this.gameStatus == GameState.BLACK_MOVE) {
      currentPlayer = black;
    } else {
      isGameOver();
      return;
    }

    try {
      this.move = currentPlayer.createMove();
      notifyGameMakeMove();
    } catch (GameException e) {
      this.move = null;
      throw e;
    }

    Color enemyColor = currentPlayer.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE;
    if (GameStateChecker.isMate(game.getCopyBoard(), enemyColor)) {
      gameStatus = gameStatus == GameState.WHITE_MOVE ? GameState.WHITE_WIN : GameState.BLACK_WIN;
    } else if (GameStateChecker.isDraw(game.getCopyBoard(), gameHistory)) {
      gameStatus = GameState.DRAW;
    } else {
      gameStatus = gameStatus == GameState.WHITE_MOVE ? GameState.BLACK_MOVE : GameState.WHITE_MOVE;
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
        winPlayer = white;
        notifyGameOver();
        return true;
      }
      case BLACK_WIN -> {
        winPlayer = black;
        notifyGameOver();
        return true;
      }
      case DRAW -> {
        winPlayer = null;
        notifyGameOver();
        return true;
      }
      default -> {
        return false;
      }
    }
  }

  public PlayerInfo getWhite() {
    return white;
  }

  public PlayerInfo getBlack() {
    return black;
  }

  public Board getBoard() {
    return game.getCopyBoard();
  }

  public PlayerInfo getCurrentPlayer() {
    return currentPlayer;
  }

}
