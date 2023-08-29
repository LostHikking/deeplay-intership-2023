package io.deeplay.grandmastery.bots;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.BotUtils;
import java.util.List;
import java.util.Map;

/** Минимакс бот с альфа-бета отсечением. */
public class Minimaximus extends Player {
  public record MoveAndEst(Move move, Integer est) {}

  private static final Map<FigureType, Integer> PRICE_MAP =
      Map.of(
          FigureType.PAWN,
          10,
          FigureType.BISHOP,
          30,
          FigureType.KNIGHT,
          30,
          FigureType.ROOK,
          50,
          FigureType.QUEEN,
          90,
          FigureType.KING,
          1000);

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

  public Minimaximus(Color color) {
    this(color, 3);
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
    var copyBoard = new HashBoard();
    Boards.copyBoard(board).accept(copyBoard);

    if (deep == 0
        || GameStateChecker.isMate(copyBoard, currentColor)
        || GameStateChecker.isMate(copyBoard, currentColor.getOpposite())
        || GameStateChecker.isDraw(copyBoard, gameHistory)) {
      return new MoveAndEst(
          lastMove,
          getEstimationForBoard(copyBoard, mainColor, gameHistory, currentColor.getOpposite()));
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
   * @param gameHistory история партии
   * @param currentColor цвет того, кто походил
   * @return Разница оценки позиций
   */
  public static int getEstimationForBoard(
      Board board, Color mainColor, GameHistory gameHistory, Color currentColor) {
    return getEstimationForColor(board, mainColor, gameHistory, currentColor)
        - getEstimationForColor(board, mainColor.getOpposite(), gameHistory, currentColor);
  }

  /**
   * Функция возвращает оценку позиции на доске для определённого цвета.
   *
   * @param board Доска
   * @param mainColor цвет того, для кого сделать оценку
   * @param gameHistory история партии
   * @param currentColor цвет того, кто походил
   * @return Оценка позиции
   */
  public static int getEstimationForColor(
      Board board, Color mainColor, GameHistory gameHistory, Color currentColor) {

    if (mainColor == currentColor) {
      if (GameStateChecker.isMate(board, mainColor.getOpposite())) {
        return getSimpleEstimationForColor(board, mainColor) + 2000;
      }
    } else {
      if (GameStateChecker.isMate(board, mainColor)) {
        return getSimpleEstimationForColor(board, mainColor) - 2000;
      }
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return getSimpleEstimationForColor(board, mainColor.getOpposite());
    }

    return getSimpleEstimationForColor(board, mainColor);
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
        .map(pos -> PRICE_MAP.get(board.getPiece(pos).getFigureType()))
        .reduce(0, Integer::sum);
  }
}
