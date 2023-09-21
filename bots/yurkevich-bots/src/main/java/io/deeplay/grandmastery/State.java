package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class State {
  private int value;
  private Board board;
  private List<State> children;
  private Color mainColor;
  private Color movingColor;
  private Color opponentColor;
  private Move move;
  private GameHistory gameHistory;
  private List<Position> mainPiecesPositions;
  private List<Position> opponentPiecesPositions;
  private boolean isMainNode;
  private boolean isTerminal;
  private Map<Position, Integer> piecesMap;

  /**
   * Конструктор для вершины.
   *
   * @param board Доска.
   * @param mainColor Цвет бота.
   * @param movingColor Цвет ходящего игрока.
   * @param move Ход, который привел к этому состоянию.
   * @param gameHistory История.
   * @param isMainNode Говорит, корневая это вершина или нет.
   */
  public State(
      Board board,
      Color mainColor,
      Color movingColor,
      Move move,
      GameHistory gameHistory,
      boolean isMainNode) {
    this.board = board;
    this.children = new ArrayList<>();
    this.mainColor = mainColor;
    this.movingColor = movingColor;
    this.opponentColor = mainColor == Color.WHITE ? Color.BLACK : Color.WHITE;
    this.move = move;
    this.gameHistory = gameHistory;
    this.isMainNode = isMainNode;
    this.piecesMap = new HashMap<>();
    this.mainPiecesPositions = board.getAllPiecePositionByColor(this.mainColor);
    this.opponentPiecesPositions = board.getAllPiecePositionByColor(this.opponentColor);
    this.value = 0;
  }
}
