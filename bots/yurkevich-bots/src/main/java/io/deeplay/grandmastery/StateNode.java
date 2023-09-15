package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateNode {
  private int value;
  private Board board;
  private List<StateNode> children;
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

  /**
   * Конструктор для вершины.
   * @param board Доска.
   * @param mainColor Цвет бота.
   * @param movingColor Цвет ходящего игрока.
   * @param move Ход, который привел к этому состоянию.
   * @param gameHistory История.
   * @param isMainNode Говорит, корневая это вершина или нет.
   */
  public StateNode(
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
    // добавляю стоимость в случае если узел терминальный
    setTerminalCost();
  }

  /**
   * Метод устанавливает стоимость состояния.
   */
  public void setCost() {
    // устанавливаю базовую стоимость
    setBasePiecesCost(mainColor);
    setBasePiecesCost(opponentColor);
    // уменьшаю стоимость фигур, если они под боем
    piecesSafety(mainColor);
    piecesSafety(opponentColor);
    // регулирую стоимость в зависимости отдаленности фигур от центра
    setCenterControlValue();
    // добавляет цену при шахе либо окружении короля
    setCheckCost(5);
    // добавляю окончательную стоимость фигур к стоимости вершины
    setFinalNodeCost();
  }

  /** Добавляет в финальную стоимость цену фигур из мапы. */
  public void setFinalNodeCost() {
    for (Map.Entry<Position, Integer> entry : piecesMap.entrySet()) {
      this.value += entry.getValue();
    }
  }

  /** Метод для увеличения стоимости фигуры, по мере ее приближения к центру. */
  public void setCenterControlValue() {
    List<Character> figureTypes = new ArrayList<>(Arrays.asList('q', 'r', 'b', 'n'));
    for (Position mainPos : mainPiecesPositions) {
      Piece piece = board.getPiece(mainPos);
      if (figureTypes.contains(piece.getFigureType().getSymbol())) {
        int distanceToCenter = distanceToCenter(mainPos);
        double controlMultiplier = calculateControlMultiplier(distanceToCenter);
        piecesMap.put(mainPos, (int) (piecesMap.get(mainPos) * controlMultiplier));
      }
    }
    for (Position oppPos : opponentPiecesPositions) {
      Piece piece = board.getPiece(oppPos);
      if (figureTypes.contains(piece.getFigureType().getSymbol())) {
        int distanceToCenter = distanceToCenter(oppPos);
        double controlMultiplier = calculateControlMultiplier(distanceToCenter);
        piecesMap.put(oppPos, (int) (piecesMap.get(oppPos) * controlMultiplier));
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
   * Вспомогательный метод для метода оценивания контроля середины.
   *
   * @param distance Дистанция до центра.
   * @return Коэффициент.
   */
  public double calculateControlMultiplier(int distance) {
    return 1 - (distance * 0.1);
  }

  /**
   * Метод установки базовой стоимости фигур с учетом приблежения пешки.
   *
   * @param color цвет фигур, которые мы рассматриваем
   */
  public void setBasePiecesCost(Color color) {
    int mul = color == mainColor ? 1 : -1;
    List<Position> positions = color == mainColor ? mainPiecesPositions : opponentPiecesPositions;
    int endGameRatio = opponentPiecesPositions.size() < 6 ? 3 : 1;

    for (Position position : positions) {
      Piece piece = board.getPiece(position);
      int pieceValue = BASE_PRICE_MAP.get(piece.getFigureType());

      if (piece.getFigureType() == FigureType.PAWN) {
        int distanceToPromotion =
            color == Color.WHITE ? 7 - position.row().value() : position.row().value();
        pieceValue += endGameRatio * (8 - distanceToPromotion) * 5;
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
   * Метод, оценивающий опасность для короля. Регулирует стоимость, обращая внимание на шах и
   * "окружение" противником.
   *
   * @param cost Цена шаха.
   */
  public void setCheckCost(int cost) {
    int surroundCost = 0;
    int mult = 0;

    Position mainKingPosition =
        (mainColor == Color.WHITE) ? board.getBlackKingPosition() : board.getWhiteKingPosition();
    surroundCost += setKingInDangerCost(mainKingPosition, opponentColor, cost);

    Position opponentKingPosition =
        (opponentColor == Color.WHITE)
            ? board.getBlackKingPosition()
            : board.getWhiteKingPosition();
    surroundCost -= setKingInDangerCost(opponentKingPosition, mainColor, cost);

    if (GameStateChecker.isCheck(board, opponentColor)) {
      mult = 1;
    } else if (GameStateChecker.isCheck(board, mainColor)) {
      mult = -1;
    }

    this.value += mult * cost + surroundCost;
  }

  /** Проверка на терминальность и оценивание состояния в случае мата. */
  public void setTerminalCost() {
    if (GameStateChecker.isMate(board, opponentColor)) {
      value += 100000;
      setTerminal(true);
    } else if (GameStateChecker.isMate(board, mainColor)) {
      value -= 100000;
      setTerminal(true);
    } else if (GameStateChecker.isDraw(board, gameHistory)) {
      setTerminal(true);
    }
  }

  /**
   * Проверяет, находятся ли позиции вокруг короля под боем и регулирует стоимость состояния. Если
   * фигура ставит шах или окружает короля, находясь под боем - такой ход оценивается 0.
   *
   * @param kingPosition позиция атакуемого короля
   * @param oppColor цвет атакующих фигур
   * @param cost ценость атакованной клетки
   * @return оценка опасности короля
   */
  public int setKingInDangerCost(Position kingPosition, Color oppColor, int cost) {
    int value = 0;
    List<Position> positionsAroundKing = new ArrayList<>();
    int kingCol = kingPosition.col().value();
    int kingRow = kingPosition.row().value();
    int[] dx = {-1, -1, -1, 1, 1, 1, 0, 0};
    int[] dy = {-1, 1, 0, -1, 1, 0, 1, -1};
    for (int i = 0; i < 8; i++) {
      int colNearKing = kingCol + dx[i];
      int rowNearKing = kingRow + dy[i];
      if (colNearKing >= 0 && colNearKing <= 7 && rowNearKing >= 0 && rowNearKing <= 7) {
        Position posNearKing = new Position(new Column(colNearKing), new Row(rowNearKing));
        if (!board.hasPiece(posNearKing)) {
          positionsAroundKing.add(posNearKing);
        } else {
          if (board.getPiece(posNearKing).getColor() != oppColor) {
            positionsAroundKing.add(posNearKing);
          }
        }
      }
    }
    List<Position> oppPositions =
        oppColor == mainColor ? mainPiecesPositions : opponentPiecesPositions;
    Piece oppPiece;
    List<Move> oppMoves;
    Position posToAttack;
    for (Position oppPos : oppPositions) {
      if (positionsAroundKing.contains(oppPos)) {
        value = 0;
        break;
      }
      oppPiece = board.getPiece(oppPos);
      oppMoves = oppPiece.getAllMoves(board, oppPos);
      for (Position posAroundKing : positionsAroundKing) {
        for (Move oppMove : oppMoves) {
          posToAttack = oppMove.to();
          if (posToAttack.equals(posAroundKing)) {
            value += cost;
          }
        }
      }
    }
    return value;
  }
}
