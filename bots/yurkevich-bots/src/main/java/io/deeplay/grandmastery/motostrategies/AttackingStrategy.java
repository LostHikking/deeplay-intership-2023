package io.deeplay.grandmastery.motostrategies;

import io.deeplay.grandmastery.State;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class AttackingStrategy extends Strategy {

  private static final Map<FigureType, Integer> BASE_PRICE_MAP =
      Map.of(
          FigureType.PAWN,
          10,
          FigureType.KNIGHT,
          30,
          FigureType.BISHOP,
          50,
          FigureType.ROOK,
          50,
          FigureType.QUEEN,
          100,
          FigureType.KING,
          1000);

  @Override
  public int evaluate(State node) {
    init(node);
    setBasePiecesCost(mainColor);
    setBasePiecesCost(opponentColor);
    setCenterControlValue(mainColor);
    setCenterControlValue(opponentColor);
    setKingAttackCost(mainColor);
    setKingAttackCost(opponentColor);
    piecesSafety(mainColor);
    piecesSafety(opponentColor);
    setFinalStateCost();
    return this.value;
  }

  /** Метод увеличения стоимости определенных фигур, по мере их приближения к центру. */
  public void setCenterControlValue(Color color) {
    List<Character> figureTypes = new ArrayList<>(Arrays.asList('q', 'r', 'b', 'n'));
    List<Position> positions = color == mainColor ? mainPiecesPositions : opponentPiecesPositions;
    for (Position pos : positions) {
      Piece piece = board.getPiece(pos);
      if (figureTypes.contains(piece.getFigureType().getSymbol())) {
        int distanceToCenter = distanceToCenter(pos);
        double controlMultiplier = calculateControlMultiplier(distanceToCenter);
        piecesMap.put(pos, (int) (piecesMap.get(pos) * controlMultiplier));
      }
    }
  }

  /**
   * Считает расстояние до центра.
   *
   * @param position Позиция фигуры.
   * @return Расстояние.
   */
  public int distanceToCenter(Position position) {
    int dx = Math.abs(position.col().value() - 4);
    int dy = Math.abs(position.row().value() - 4);
    return dx + dy;
  }

  /**
   * Вспомогательный метод для подсчёта коэффициента контроля центра.
   *
   * @param distance Дистанция до центра.
   * @return Коэффициент.
   */
  public double calculateControlMultiplier(int distance) {
    return 1 + (0.5 * (1 - (distance / 8.0)));
  }

  /**
   * Метод установки базовой стоимости фигур с учетом приближения пешки.
   *
   * @param color цвет фигур, которые мы рассматриваем
   */
  public void setBasePiecesCost(Color color) {
    int mul = color == mainColor ? 1 : -1;
    List<Position> positions = color == mainColor ? mainPiecesPositions : opponentPiecesPositions;
    int endGameRatio = opponentPiecesPositions.size() < 5 ? 4 : 1;

    for (Position position : positions) {
      Piece piece = board.getPiece(position);
      int pieceValue = BASE_PRICE_MAP.get(piece.getFigureType());

      if (piece.getFigureType() == FigureType.PAWN && piece.isMoved()) {
        int distanceToPromotion =
            color == Color.WHITE ? 7 - position.row().value() : position.row().value();
        pieceValue = (pieceValue + endGameRatio * (8 - distanceToPromotion) * 2);
      }

      if (!piecesMap.containsKey(position)) {
        piecesMap.put(position, mul * pieceValue);
      } else {
        piecesMap.put(position, piecesMap.get(position) + mul * pieceValue);
      }
    }
  }

  /**
   * Метод, регулирующий стоимость фигуры, проверяя, под боем ли она.
   *
   * @param color Цвет фигур, стоимость которых мы регулируем
   */
  public void piecesSafety(Color color) {
    List<Position> checkedPiecesPositions =
        (color == mainColor) ? mainPiecesPositions : opponentPiecesPositions;
    List<Position> attackingPiecesPositions =
        (color == mainColor) ? opponentPiecesPositions : mainPiecesPositions;
    boolean isAttacked;

    for (Position checkedPos : checkedPiecesPositions) {
      isAttacked = false;
      for (Position attackingPos : attackingPiecesPositions) {
        Piece attackingPiece = board.getPiece(attackingPos);
        for (Move attackingMove : attackingPiece.getAllMoves(board, attackingPos)) {
          if (attackingMove.to().equals(checkedPos)) {
            piecesMap.put(checkedPos, (int) (0.3 * piecesMap.get(checkedPos)));
            isAttacked = true;
            break;
          }
        }
        if (isAttacked) {
          break;
        }
      }
    }
  }

  /**
   * Метод, подсчитывающий коэффициент "дистанции до вражеского короля".
   *
   * @param firstPosition Позиция атакующей фигуры.
   * @param secondPosition Позиция атакуемой фигуры.
   * @return Коэффициент.
   */
  public double distanceToKingRatio(Position firstPosition, Position secondPosition) {
    int distance =
        Math.abs(firstPosition.col().value() - secondPosition.col().value())
            + Math.abs(firstPosition.row().value() - secondPosition.row().value());

    return distance <= 2 ? 1 : 1 + (double) 1 / distance;
  }

  /**
   * Метод корректирует стоимость фигур, в зависимости от опасности, которую они доставляют
   * вражескому королю.
   *
   * @param kingColor Цвет вражеского короля.
   */
  public void setKingAttackCost(Color kingColor) {
    Position kingAttackerPos = null;
    if (GameStateChecker.isCheck(board, kingColor)) {
      kingAttackerPos = move.to();
    }
    Position kingPosition;
    List<Position> attackingPiecesPositions;
    if (kingColor == Color.WHITE) {
      kingPosition = board.getWhiteKingPosition();
      attackingPiecesPositions = board.getAllPiecePositionByColor(Color.BLACK);
    } else {
      kingPosition = board.getBlackKingPosition();
      attackingPiecesPositions = board.getAllPiecePositionByColor(Color.WHITE);
    }
    int lastValue;
    double checkRatio;
    double distanceToKingRatio;
    List<Character> figureTypes = new ArrayList<>(Arrays.asList('q', 'r', 'b', 'n'));
    Piece piece;
    for (Position pos : attackingPiecesPositions) {
      piece = board.getPiece(pos);
      if (figureTypes.contains(piece.getFigureType().getSymbol())) {
        checkRatio = 1;
        distanceToKingRatio = 1;
        lastValue = piecesMap.get(pos);
        if (pos.equals(kingAttackerPos)) {
          checkRatio = 2;
        }
        if (piece.isMoved()) {
          distanceToKingRatio = distanceToKingRatio(pos, kingPosition);
        }
        piecesMap.put(pos, (int) (lastValue * checkRatio * distanceToKingRatio));
      }
    }
  }

  /** Проверка на терминальность и оценивание состояния в случае мата/ничьей. */
  @Override
  public void setTerminalCost(State state) {
    GameHistory gameHistory = state.getGameHistory();
    Board board = state.getBoard();
    Color mainColor = state.getMainColor();
    Color opponentColor = state.getOpponentColor();
    if (GameStateChecker.isMate(board, opponentColor)) {
      state.setValue(100000);
      state.setTerminal(true);
    } else if (GameStateChecker.isMate(board, mainColor)) {
      state.setValue(-100000);
      state.setTerminal(true);
    } else if (GameStateChecker.isDraw(board, gameHistory)) {
      state.setValue(-100000);
      state.setTerminal(true);
    }
  }
}
