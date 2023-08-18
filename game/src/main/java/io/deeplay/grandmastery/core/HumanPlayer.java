package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.listeners.InputListener;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.IOException;

/** Дочерний класс класса Player, представляет реального игрока. */
public class HumanPlayer extends Player {
  private final InputListener inputListener;

  /**
   * Конструктор для плеера.
   *
   * @param name Имя
   * @param color Цвет
   */
  public HumanPlayer(String name, Color color, InputListener inputListener) {
    super(name, color);
    this.inputListener = inputListener;
  }

  private String notifyInputMoveListener() throws IOException {
    return inputListener.inputMove(this.getName());
  }

  private boolean notifyConfirmSurListener() throws IOException {
    return inputListener.confirmSur();
  }

  private boolean notifyAnswerDrawListener() throws IOException {
    return inputListener.answerDraw();
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean answerDraw() throws GameException {
    try {
      return notifyAnswerDrawListener();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
