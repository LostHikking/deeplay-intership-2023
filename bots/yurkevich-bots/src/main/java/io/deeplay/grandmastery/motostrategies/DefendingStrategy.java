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
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class DefendingStrategy extends Strategy {

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
          300,
          FigureType.KING,
          2000);

  @Override
  public int evaluate(State node) {
    init(node);
    setBasePiecesCost(mainColor);
    setBasePiecesCost(opponentColor);
    piecesSafety(mainColor);
    piecesSafety(opponentColor);
    setFinalStateCost();
    return this.value;
  }

  /**
   * Метод установки базовой стоимости фигур.
   *
   * @param color цвет фигур, которые мы рассматриваем
   */
  public void setBasePiecesCost(Color color) {
    int mul = color == mainColor ? 1 : -1;
    List<Position> positions = color == mainColor ? mainPiecesPositions : opponentPiecesPositions;
    for (Position position : positions) {
      Piece piece = board.getPiece(position);
      int pieceValue = BASE_PRICE_MAP.get(piece.getFigureType());
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
            piecesMap.put(checkedPos, (-1 * piecesMap.get(checkedPos)));
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
      state.setValue(100000);
      state.setTerminal(true);
    }
  }
}
