package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.IOException;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  private final UI ui;
  private final boolean withUi;
  private boolean start = false;

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param color Цвет
   * @param ui пользовательский интерфейс
   */
  public HumanPlayer(String name, Color color, UI ui) {
    super(name, color);
    this.ui = ui;
    this.withUi = true;
  }

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param color Цвет
   * @param ui пользовательский интерфейс
   * @param withUi {@code false}, чтобы отключить вывод в консоль
   */
  public HumanPlayer(String name, Color color, UI ui, boolean withUi) {
    super(name, color);
    this.ui = ui;
    this.withUi = withUi;
  }

  private String notifyInputMoveListener() throws IOException {
    return ui.inputMove(this.getName());
  }

  private boolean notifyConfirmSurListener() throws IOException {
    return ui.confirmSur();
  }

  private boolean notifyAnswerDrawListener() throws IOException {
    return ui.answerDraw();
  }

  /**
   * Метод, отвечающий за ввод хода игрока.
   *
   * @return {@code Move} ход игрока.
   */
  @Override
  public Move createMove() throws GameException {
    if (this.isGameOver()) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }

    try {
      String input = null;
      while (input == null) {
        input = notifyInputMoveListener();
        if ("sur".equalsIgnoreCase(input) || "surrender".equalsIgnoreCase(input)) {
          if (notifyConfirmSurListener()) {
            return new Move(null, null, null, MoveType.SURRENDER);
          } else {
            input = null;
            continue;
          }
        }

        if ("draw".equalsIgnoreCase(input)) {
          return new Move(null, null, null, MoveType.DRAW_OFFER);
        }
      }

      Move move = LongAlgebraicNotation.getMoveFromString(input);
      setLastMove(move);
      return move;
    } catch (GameException ex) {
      if (withUi) {
        ui.incorrectMove();
      }
      lastMove = null;

      throw ex;
    } catch (IOException e) {
      throw new GameException(GameErrorCode.ERROR_PLAYER_INPUT, e);
    }
  }

  @Override
  public boolean answerDraw() throws GameException {
    try {
      return notifyAnswerDrawListener();
    } catch (IOException e) {
      throw new GameException(GameErrorCode.ERROR_PLAYER_INPUT, e);
    }
  }

  @Override
  public void startup(Board board) throws GameException {
    super.startup(board);
    try {
      if (withUi && !start) {
        ui.printHelp();
        ui.showBoard(board, getColor());
        start = true;
      }
    } catch (IOException e) {
      throw new GameException(GameErrorCode.ERROR_START_GAME, e);
    }
  }

  @Override
  public void makeMove(Move move) throws GameException {
    try {
      super.makeMove(move);

      Board board = getBoard();
      if (withUi) {
        if (move.equals(getLastMove())) {
          ui.showMove(move, color);
          ui.showBoard(board, getColor());
        } else {
          ui.showMove(move, color == Color.WHITE ? Color.BLACK : Color.WHITE);
          ui.showBoard(board, getColor());
        }
      }
    } catch (GameException e) {
      if (withUi) {
        ui.incorrectMove();
      }
      lastMove = null;

      throw e;
    }
  }

  @Override
  public void gameOver(GameState gameState) {
    super.gameOver(gameState);
    if (withUi) {
      ui.showResultGame(gameState);
    }
  }
}
