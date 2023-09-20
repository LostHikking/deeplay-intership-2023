package io.deeplay.grandmastery.algorithms;

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

/** Класс, предоставляющий методы для оценки текущей игровой позиции. */
class Evaluation {
  /** Максимальное значение оценки позиции. */
  public static final double MAX_EVAL = 1.0;

  /** Минимальное значение оценки позиции. */
  public static final double MIN_EVAL = -1.0;

  /** Вес (оценка) каждой фигуры. */
  private static final Map<FigureType, Double> PIECE_PRICE =
      Map.of(
          FigureType.PAWN,
          1.0,
          FigureType.KNIGHT,
          3.0,
          FigureType.BISHOP,
          3.0,
          FigureType.ROOK,
          5.0,
          FigureType.QUEEN,
          9.0,
          FigureType.KING,
          100.0);

  /**
   * Главная оценочная функция.
   *
   * @param board Доска.
   * @param gameHistory История игры.
   * @param botColor Цвет бота.
   * @param botMove Флаг, указывающий, ходит ли сейчас бот.
   * @return Оценка текущей позиции.
   */
  public static double evaluationFunc(
      Board board, GameHistory gameHistory, Color botColor, boolean botMove) {
    if (GameStateChecker.isMate(board, botColor.getOpposite())) {
      return MAX_EVAL;
    } else if (GameStateChecker.isMate(board, botColor)) {
      return MIN_EVAL;
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return 0;
    }

    double ourRate = evaluationBoard(board, gameHistory, botColor);
    double enemyRate = evaluationBoard(board, gameHistory, botColor.getOpposite());
    if (!botMove) {
      ourRate += pieceExchange(board, botColor);
    } else {
      enemyRate += pieceExchange(board, botColor.getOpposite());
    }

    double result = (ourRate - enemyRate) * (1 + 10 / (ourRate + enemyRate)) / 1000;
    result = Math.round(result * 1e9) / 1e9;
    return result;
  }

  /**
   * Оценивает текущую позицию на доске, с учетом различных факторов, таких как фигуры на доске и
   * бонусы.
   *
   * @param board Доска.
   * @param gameHistory История игры.
   * @param color Цвет для которого проводится оценка.
   * @return Оценка текущей позиции с учетом различных факторов.
   */
  protected static double evaluationBoard(Board board, GameHistory gameHistory, Color color) {
    return kingEndgameEval(board, color)
        + calculatePiecesPrice(board, color)
        + Bonuses.middlegame(board, gameHistory, color)
        + Bonuses.openLines(board, color, gameHistory.getMoves().size());
  }

  /**
   * Оценивает позицию короля в конце игры (эндшпиле).
   *
   * @param board Доска.
   * @param color Цвет короля.
   * @return Оценка позиции короля.
   */
  protected static double kingEndgameEval(Board board, Color color) {
    Position kingPos = board.getKingPositionByColor(color);
    Position enemyKingPos = board.getKingPositionByColor(color.getOpposite());
    if (board.getAllPiecePositionByColor(color).size() != 1) {
      return 0.0;
    }

    int row = kingPos.row().value();
    int col = kingPos.col().value();

    int rowCenter = row <= 3 ? 3 : 4;
    int colCenter = col <= 3 ? 3 : 4;
    int rowDistForCenter = Math.abs(row - rowCenter);
    int colDistForCenter = Math.abs(col - colCenter);

    int rowDistForEnemyKing = Math.abs(row - enemyKingPos.row().value());
    int colDistForEnemyKing = Math.abs(col - enemyKingPos.col().value());

    int moveCount = board.getPiece(kingPos).getAllMoves(board, kingPos).size();

    return (rowDistForCenter + colDistForCenter) * -1
        + ((moveCount + rowDistForEnemyKing + colDistForEnemyKing) * 0.1);
  }

  /**
   * Рассчитывает стоимость фигур на доске для заданного цвета.
   *
   * @param board Доска.
   * @param color Цвет фигур, для которых рассчитывается стоимость.
   * @return Суммарная стоимость фигур на доске для заданного цвета.
   */
  protected static double calculatePiecesPrice(Board board, Color color) {
    return board.getAllPiecePositionByColor(color).stream()
        .map(pos -> calculatePiecePrice(board, pos, color))
        .reduce(0.0, Double::sum);
  }

  /**
   * Рассчитывает стоимость одной фигуры на доске для заданного цвета.
   *
   * @param board Доска.
   * @param pos Позиция фигуры.
   * @param color Цвет фигур, для которых рассчитывается стоимость.
   * @return Стоимость одной фигуры на доске для заданного цвета.
   */
  protected static double calculatePiecePrice(Board board, Position pos, Color color) {
    return board.getPiece(pos).getFigureType() == FigureType.PAWN
        ? calculatePawnPrice(board, pos, color)
        : PIECE_PRICE.get(board.getPiece(pos).getFigureType());
  }

  /**
   * Оценивает стоимость пешки на доске для заданного цвета.
   *
   * @param board Доска.
   * @param pos Позиция пешки.
   * @param color Цвет пешки.
   * @return Стоимости пешки на доске для заданного цвета.
   */
  private static double calculatePawnPrice(Board board, Position pos, Color color) {
    int row = pos.row().value();
    int col = pos.col().value();

    double rowPrice =
        PIECE_PRICE.get(FigureType.PAWN) + (color == Color.WHITE ? row : 7 - row) * 0.1 - 0.1;
    double centerBonus =
        col > 2 && col < 5 && row > 2 && row < 5 ? (4 - (col % 4 + col % 3)) * 0.1 : 0;
    double doublePawnPenalty = 0;
    if (board.hasPiece(col, row + 1)
        && board.getPiece(col, row + 1).getFigureType() == FigureType.PAWN) {
      doublePawnPenalty += 0.5;
    }
    if (board.hasPiece(col, row - 1)
        && board.getPiece(col, row - 1).getFigureType() == FigureType.PAWN) {
      doublePawnPenalty += 0.5;
    }

    return rowPrice + centerBonus - doublePawnPenalty;
  }

  /**
   * Рассчитывает штраф за незащищённую фигуру под боем на ходу противника.
   *
   * @param board Доска.
   * @param color Цвет фигур, для которых рассчитывается штраф.
   * @return Штраф.
   */
  protected static double pieceExchange(Board board, Color color) {
    double result = 0;
    boolean isSecurity;
    List<Position> friendlies = board.getAllPiecePositionByColor(color);
    List<Position> enemies = board.getAllPiecePositionByColor(color.getOpposite());

    for (Position friendly : friendlies) {
      if (!friendly.equals(board.getKingPositionByColor(color))) {
        isSecurity = isSecurity(board, friendly, color);

        for (Position enemy : enemies) {
          Piece enemyPiece = board.getPiece(enemy);
          FigureType promotionPiece = null;
          if (enemyPiece.getFigureType() == FigureType.PAWN
              && (friendly.row().value() == 0 || friendly.row().value() == 7)) {
            promotionPiece = FigureType.QUEEN;
          }

          Move move = new Move(enemy, friendly, promotionPiece);
          if (!isSecurity && enemyPiece.canMove(board, move)) {
            result -= calculatePiecePrice(board, friendly, color);
            break;
          }
        }
      }
    }
    return result;
  }

  /**
   * Проверяет, защищена ли фигура (под боем дружественных фигур).
   *
   * @param board Доска.
   * @param pos Позиция фигуры, которую нужно проверить на безопасность.
   * @param color Цвет фигуры.
   * @return {@code true}, если фигура защищена, иначе {@code false}.
   */
  protected static boolean isSecurity(Board board, Position pos, Color color) {
    List<Position> friendlies = board.getAllPiecePositionByColor(color);

    for (Position friendly : friendlies) {
      if (!friendly.equals(pos)) {
        Piece piece = board.getPiece(friendly);
        FigureType promotionPiece = null;
        if (piece.getFigureType() == FigureType.PAWN
            && (pos.row().value() == 0 || pos.row().value() == 7)) {
          promotionPiece = FigureType.QUEEN;
        }

        Move move = new Move(friendly, pos, promotionPiece);
        if (piece.canMove(board, move, true, false)) {
          return true;
        }
      }
    }
    return false;
  }
}
