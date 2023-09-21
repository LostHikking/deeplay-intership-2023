package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.domain.MoveType;
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

  private final List<GameListener> historyAndPlayers;

  private final Player white;

  private final Player black;

  @Getter private final GameHistory gameHistory;

  private final Game game;

  @Getter private GameState gameStatus;

  @Getter private Move move;

  @Getter private boolean isSurrender = false;
  @Getter private boolean isDrawWithOffer = false;

  /** Дефолтный конструктор класса {@code GameController}. */
  public GameController(Player firstPlayer, Player secondPlayer) {
    this.move = null;
    this.historyAndPlayers = new ArrayList<>();

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
    this.historyAndPlayers.add(gameHistory);
    this.historyAndPlayers.add(white);
    this.historyAndPlayers.add(black);
  }

  private void notifyGameStartup(Board board) {
    gameListener.startup(board);
    for (GameListener listener : historyAndPlayers) {
      listener.startup(board);
    }
  }

  private void notifyGameMakeMove() throws GameException {
    gameListener.makeMove(move);
    gameHistory.addBoard(game.getCopyBoard());
    for (GameListener listener : historyAndPlayers) {
      listener.makeMove(move);
    }
  }

  private void notifyGameOver() throws GameException {
    gameListener.gameOver(gameStatus);
    for (GameListener listener : historyAndPlayers) {
      listener.gameOver(gameStatus);
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
  }

  /**
   * Запускает игру по доске.
   *
   * @param customBoard Доска
   * @throws GameException если возникла ошибка во время игры
   */
  public void beginPlay(Board customBoard) throws GameException {
    Board board = new HashBoard();
    Boards.copy(customBoard).accept(board);

    if (board.getWhiteKingPosition() == null || board.getBlackKingPosition() == null) {
      throw GameErrorCode.ERROR_START_GAME.asException();
    }
    notifyGameStartup(board);
    gameStatus = GameState.WHITE_MOVE;
  }

  /**
   * Выполняет следующий игровой ход в соответствии с текущим состоянием игры.
   *
   * @throws GameException если возникла ошибка во время игры
   */
  public void nextMove() throws GameException {
    try {
      Player currentPlayer = (Player) getCurrentPlayer();
      this.move = currentPlayer.createMove();
      if (checkAnyTypeMove()) {
        return;
      }

      notifyGameMakeMove();
    } catch (GameException e) {
      this.move = null;
      throw e;
    }

    Color enemyColor = getOpponentPlayer().getColor();
    if (GameStateChecker.isMate(game.getCopyBoard(), enemyColor)) {
      gameStatus = gameStatus == GameState.WHITE_MOVE ? GameState.WHITE_WIN : GameState.BLACK_WIN;
      notifyGameOver();
    } else if (GameStateChecker.isDraw(game.getCopyBoard(), gameHistory)) {
      gameStatus = GameState.DRAW;
      notifyGameOver();
    } else {
      gameStatus = gameStatus == GameState.WHITE_MOVE ? GameState.BLACK_MOVE : GameState.WHITE_MOVE;
    }

    this.move = null;
  }

  /**
   * Проверяет, предлагает ли игрок ничью или сдается.
   *
   * @return {@code true}, если да, иначе {@code false}
   */
  private boolean checkAnyTypeMove() {
    if (this.move == null) {
      throw GameErrorCode.ERROR_PLAYER_MAKE_MOVE.asException();
    }

    if (move.moveType() == MoveType.TECHNICAL_DEFEAT) {
      gameStatus =
          gameStatus == GameState.WHITE_MOVE
              ? GameState.TECHNICAL_DEFEAT_WHITE
              : GameState.TECHNICAL_DEFEAT_BLACK;
      notifyGameOver();
      return true;
    }

    if (move.moveType() == MoveType.SURRENDER) {
      gameStatus = gameStatus == GameState.WHITE_MOVE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
      isSurrender = true;
      notifyGameOver();
      return true;
    }

    if (move.moveType() == MoveType.DRAW_OFFER) {
      Player opponent = (Player) getOpponentPlayer();
      if (opponent.answerDraw()) {
        gameStatus = GameState.DRAW;
        isDrawWithOffer = true;
        notifyGameOver();
      }
      return true;
    }
    return false;
  }

  /**
   * Проверяет, завершилась ли игра.
   *
   * @return {@code true}, если игра завершилась, иначе {@code false}
   */
  public boolean isGameOver() {
    switch (gameStatus) {
      case WHITE_WIN, BLACK_WIN, TECHNICAL_DEFEAT_WHITE, TECHNICAL_DEFEAT_BLACK, DRAW -> {
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
    return gameStatus == GameState.WHITE_MOVE ? white : black;
  }

  public PlayerInfo getOpponentPlayer() {
    return gameStatus == GameState.WHITE_MOVE ? black : white;
  }
}
