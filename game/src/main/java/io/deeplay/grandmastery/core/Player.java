package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.listeners.GameListener;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;

/** Абстрактный класс, представляющий игрока. */
public abstract class Player implements GameListener, PlayerInfo {
  private final String name;

  private final Color color;

  private final Board board;

  private String lastMove;

  private boolean gameOver;

  /** Конструктор по умолчанию. */
  public Player() {
    this.name = "";
    this.color = Color.WHITE;
    this.board = new HashBoard();
  }

  /**
   * Конструктор с параметрами.
   *
   * @param name Имя
   * @param color Цвет
   */
  public Player(String name, Color color) {
    this.name = name;
    this.color = color;
    this.board = new HashBoard();
  }

  /**
   * Метод, записывающий ход игрока.
   *
   * @param move Ход игрока
   * @throws GameException Если ход не валиден.
   */
  public void setLastMove(Move move) throws GameException {
    this.lastMove = LongAlgebraicNotation.moveToString(move);
  }

  public void setLastMove(String move) throws GameException {
    this.lastMove = move;
  }

  /**
   * Метод, который создает ход.
   *
   * @return {@code Move} ход игрока.
   * @throws GameException Если ход не валиден.
   */
  public abstract Move createMove() throws GameException;

  /**
   * Метод для принятия/отказа ничьи игроком.
   *
   * @return {@code true} если принял, {@code false} если отказался.
   * @throws GameException Если произошла ошибка ввода.
   */
  public abstract boolean answerDraw() throws GameException;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getLastMove() {
    return lastMove;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public void startup(Board board) {
    Boards.copyBoard(board).accept(this.board);
    gameOver = false;
  }

  @Override
  public void makeMove(Move move) {
    Piece piece = board.getPiece(move.from());

    if (piece.getFigureType() == FigureType.KING) {
      piece.move(board, move);
    } else {
      board.removePiece(move.from());
      board.removePiece(move.to());

      if (move.promotionPiece() != null) {
        board.setPiece(move.to(), move.promotionPiece().getPiece(piece.getColor()));
      } else {
        board.setPiece(move.to(), piece);
      }
      piece.setMoved(true);
    }
  }

  @Override
  public void gameOver() {
    gameOver = true;
  }

  public Board getBoard() {
    return board;
  }

  public boolean isGameOver() {
    return gameOver;
  }
}
