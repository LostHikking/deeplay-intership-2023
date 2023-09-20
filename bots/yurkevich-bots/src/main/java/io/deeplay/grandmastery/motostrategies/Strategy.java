package io.deeplay.grandmastery.motostrategies;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/** Интерфейс для стратегий. */
@Getter
@Setter
public abstract class Strategy {
  protected Board board;
  protected Color mainColor;
  protected Color opponentColor;
  protected Move move;
  protected List<Position> mainPiecesPositions;
  protected List<Position> opponentPiecesPositions;
  protected Map<Position, Integer> piecesMap;
  protected int value;

  public abstract int evaluate(State state);

  public abstract void setTerminalCost(State state);

  /**
   * Метод инициализации объекта.
   *
   * @param state Игровое состояние, которое мы передаём в стратегию.
   */
  public void init(State state) {
    this.board = state.getBoard();
    this.mainColor = state.getMainColor();
    this.opponentColor = state.getOpponentColor();
    this.move = state.getMove();
    this.piecesMap = new HashMap<>();
    this.mainPiecesPositions = state.getMainPiecesPositions();
    this.opponentPiecesPositions = state.getOpponentPiecesPositions();
    this.value = state.getValue();
  }

  /** Метод берет все фигуры из мапы и складывает стоимости, формируя окончательную. */
  public void setFinalStateCost() {
    for (Map.Entry<Position, Integer> entry : piecesMap.entrySet()) {
      this.value += entry.getValue();
    }
  }
}
