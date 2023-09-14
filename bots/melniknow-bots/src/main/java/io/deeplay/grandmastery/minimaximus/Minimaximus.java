package io.deeplay.grandmastery.minimaximus;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.BotUtils;
import java.util.List;
import java.util.Map;

/** Минимакс бот с альфа-бета отсечением. */
public class Minimaximus extends Player {
  public record MoveAndEst(Move move, Integer est) {}

  private static final Integer[][] king = {
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 15, 0, 0, 0, 0, 10, 0}
  };
  private static final Integer[][] queen = {
    {-20, -10, -10, -5, -5, -10, -10, -20},
    {-10, 0, 0, 0, 0, 0, 0, -10},
    {-10, 0, 5, 5, 5, 5, 0, -10},
    {-5, 0, 5, 5, 5, 5, 0, -5},
    {0, 0, 5, 5, 5, 5, 0, -5},
    {-10, 5, 5, 5, 5, 5, 0, -10},
    {-10, 0, 5, 0, 0, 0, 0, -10},
    {-20, -10, -10, -5, -5, -10, -10, -20}
  };
  private static final Integer[][] rook = {
    {0, 0, 0, 0, 0, 0, 0, 0},
    {5, 10, 10, 10, 10, 10, 10, 5},
    {-5, 0, 0, 0, 0, 0, 0, -5},
    {-5, 0, 0, 0, 0, 0, 0, -5},
    {-5, 0, 0, 0, 0, 0, 0, -5},
    {-5, 0, 0, 0, 0, 0, 0, -5},
    {-5, 0, 0, 0, 0, 0, 0, -5},
    {0, 0, 0, 5, 5, 0, 0, 0}
  };
  private static final Integer[][] knight = {
    {-50, -40, -30, -30, -30, -30, -40, -50},
    {-40, -20, 0, 0, 0, 0, -20, -40},
    {-30, 0, 10, 15, 15, 10, 0, -30},
    {-30, 5, 15, 20, 20, 15, 5, -30},
    {-30, 0, 15, 20, 20, 15, 0, -30},
    {-30, 5, 10, 15, 15, 10, 5, -30},
    {-40, -20, 0, 5, 5, 0, -20, -40},
    {-50, -40, -30, -30, -30, -30, -40, -50}
  };
  private static final Integer[][] bishop = {
    {-20, -10, -10, -10, -10, -10, -10, -20},
    {-10, 0, 0, 0, 0, 0, 0, -10},
    {-10, 0, 5, 10, 10, 5, 0, -10},
    {-10, 5, 5, 10, 10, 5, 5, -10},
    {-10, 0, 10, 10, 10, 10, 0, -10},
    {-10, 10, 10, 10, 10, 10, 10, -10},
    {-10, 5, 0, 0, 0, 0, 5, -10},
    {-20, -10, -10, -10, -10, -10, -10, -20}
  };
  private static final Integer[][] pawn = {
    {0, 0, 0, 0, 0, 0, 0, 0},
    {50, 50, 50, 50, 50, 50, 50, 50},
    {10, 10, 20, 30, 30, 20, 10, 10},
    {5, 5, 10, 25, 25, 10, 5, 5},
    {0, 0, 0, 20, 20, 0, 0, 0},
    {5, -5, -10, 0, 0, -10, -5, 5},
    {5, 10, 10, -20, -20, 10, 10, 5},
    {0, 0, 0, 0, 0, 0, 0, 0}
  };

  private static final Map<FigureType, Integer[][]> PRICE_SQUARE_MAP =
      Map.of(
          FigureType.KING, king,
          FigureType.QUEEN, queen,
          FigureType.ROOK, rook,
          FigureType.BISHOP, bishop,
          FigureType.KNIGHT, knight,
          FigureType.PAWN, pawn);

  private static final Map<FigureType, Integer> PRICE_MAP =
      Map.of(
          FigureType.PAWN,
          100,
          FigureType.BISHOP,
          300,
          FigureType.KNIGHT,
          300,
          FigureType.ROOK,
          500,
          FigureType.QUEEN,
          900,
          FigureType.KING,
          10000);

  private final int deep;

  /**
   * Конструктор с параметрами.
   *
   * @param color цвет
   * @param deep глубина
   */
  public Minimaximus(Color color, int deep) {
    super("Melniknow-minimaximus", color);
    this.deep = deep;
  }

  @Override
  public Move createMove() throws GameException {
    if (this.isGameOver()) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }

    var moveAndEst =
        startMiniMax(
            deep,
            getBoard(),
            color,
            lastMove,
            color,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            gameHistory,
            BotUtils.getPossibleMoves(getBoard(), color));

    this.setLastMove(moveAndEst.move);
    return moveAndEst.move;
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }

  /**
   * Функция запускает алгоритм минимакса с альфа-бета отсечением.
   *
   * @param deep глубина алгоритма
   * @param board доска
   * @param currentColor цвет, который ходит
   * @param lastMove последний сделанный на доске ход
   * @param mainColor цвет, для которого считается оценка
   * @param alpha альфа
   * @param beta бета
   * @param gameHistory история партии
   * @return лучший ход и его оценка
   */
  public static MoveAndEst startMiniMax(
      int deep,
      Board board,
      Color currentColor,
      Move lastMove,
      Color mainColor,
      int alpha,
      int beta,
      GameHistory gameHistory,
      List<Move> possibleMove) {

    var isDraw = GameStateChecker.isDraw(board, gameHistory);
    var isMate = GameStateChecker.isMate(board, currentColor);

    if (deep == 0 || isMate || isDraw) {
      return new MoveAndEst(
          lastMove, getEstimationForBoard(board, mainColor, isDraw, isMate, currentColor));
    }

    var isMax = currentColor == mainColor;
    var moveAndEst = new MoveAndEst(null, isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE);

    if (possibleMove.isEmpty()) {
      return moveAndEst;
    }

    for (Move move : possibleMove) {
      var tempBoard = BotUtils.getCopyBoardAfterMove(move, board);
      tempBoard.setLastMove(move);

      var tempGameHistory = gameHistory.getCopy();
      tempGameHistory.addBoard(tempBoard);
      tempGameHistory.makeMove(move);

      var recursiveValue =
          startMiniMax(
              deep - 1,
              tempBoard,
              currentColor.getOpposite(),
              move,
              mainColor,
              alpha,
              beta,
              tempGameHistory,
              BotUtils.getPossibleMoves(tempBoard, currentColor.getOpposite()));

      if (isMax) {
        alpha = Math.max(alpha, recursiveValue.est);
        if (moveAndEst.est < recursiveValue.est) {
          moveAndEst = new MoveAndEst(move, recursiveValue.est);
        }

      } else {
        beta = Math.min(beta, recursiveValue.est);
        if (moveAndEst.est > recursiveValue.est) {
          moveAndEst = new MoveAndEst(move, recursiveValue.est);
        }
      }

      if (beta <= alpha) {
        break;
      }
    }

    return moveAndEst;
  }

  /**
   * Функция возвращает разницу оценки позиций на доске для определённого цвета.
   *
   * @param board Доска
   * @param mainColor Цвет
   * @param isDraw история партии
   * @return Разница оценки позиций
   */
  public static int getEstimationForBoard(
      Board board, Color mainColor, boolean isDraw, boolean isMate, Color current) {
    if (isMate) {
      if (mainColor == current) {
        return -20000;
      } else {
        return 20000;
      }
    }

    if (isDraw) {
      return getSimpleEstimationForColor(board, mainColor.getOpposite())
          - getSimpleEstimationForColor(board, mainColor);
    }
    return getSimpleEstimationForColor(board, mainColor)
        - getSimpleEstimationForColor(board, mainColor.getOpposite());
  }

  /**
   * Функция делает простую оценку доски для определённого цвета.
   *
   * @param board доска
   * @param color цвет
   * @return оценка
   */
  public static int getSimpleEstimationForColor(Board board, Color color) {
    return board.getAllPiecePositionByColor(color).stream()
        .map(
            pos -> {
              var pieceType = board.getPiece(pos).getFigureType();
              return PRICE_MAP.get(pieceType) + getPriceFromSquareTable(color, pos, pieceType);
            })
        .reduce(0, Integer::sum);
  }

  private static int getPriceFromSquareTable(Color color, Position pos, FigureType pieceType) {
    var arr = PRICE_SQUARE_MAP.get(pieceType);

    if (color == Color.WHITE) {
      return arr[7 - pos.row().value()][pos.col().value()];
    } else {
      return arr[pos.row().value()][pos.col().value()];
    }
  }
}
