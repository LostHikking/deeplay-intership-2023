package io.deeplay.grandmastery.minimaximus;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.BotUtils;
import java.util.ArrayList;
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
            getPossibleMoves(getBoard(), color));

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
    var board1 = new HashBoard();
    var board2 = new HashBoard();
    var board3 = new HashBoard();
    var board4 = new HashBoard();
    Boards.copyBoard(board).accept(board1);
    Boards.copyBoard(board).accept(board2);
    Boards.copyBoard(board).accept(board3);
    Boards.copyBoard(board).accept(board4);

    if (deep == 0
        || GameStateChecker.isMate(board1, currentColor)
        || GameStateChecker.isMate(board4, BotUtils.getOtherColor(currentColor))
        || GameStateChecker.isDraw(board2, gameHistory.getCopy())) {
      return new MoveAndEst(
          lastMove,
          getEstimationForBoard(
              board3, mainColor, gameHistory.getCopy(), BotUtils.getOtherColor(currentColor)));
    }

    var isMax = currentColor == mainColor;
    var moveAndEst = new MoveAndEst(null, isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE);

    if (possibleMove.isEmpty()) {
      return moveAndEst;
    }

    for (Move move : possibleMove) {
      var tempBoard = getTempBoardForMinimax(move, board);
      tempBoard.setLastMove(move);

      var tempGameHistory = gameHistory.getCopy();
      tempGameHistory.addBoard(tempBoard);
      tempGameHistory.makeMove(move);

      var recursiveValue =
          startMiniMax(
              deep - 1,
              tempBoard,
              BotUtils.getOtherColor(currentColor),
              move,
              mainColor,
              alpha,
              beta,
              tempGameHistory,
              getPossibleMoves(tempBoard, BotUtils.getOtherColor(currentColor)));

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
   * Функция возвращает новую доску, после хода, не меняя передаваемую доску.
   *
   * @param board Доска
   * @param move Ход
   * @return Новая доска
   */
  public static Board getTempBoardForMinimax(Move move, Board board) {
    var tempBoard = new HashBoard();
    Boards.copyBoard(board).accept(tempBoard);
    var piece = tempBoard.getPiece(move.from());
    piece.move(tempBoard, move);

    return tempBoard;
  }

  /**
   * Функция возвращает список доступных ходов для определённого цвета на доске.
   *
   * @param board Доска
   * @param color Цвет
   * @return Список ходов
   */
  public static List<Move> getPossibleMoves(Board board, Color color) {
    var possibleMove = new ArrayList<Move>();
    var positions = board.getAllPieceByColorPosition(color);

    for (Position position : positions) {
      possibleMove.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return possibleMove;
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
        - getEstimationForColor(
            board, BotUtils.getOtherColor(mainColor), gameHistory, currentColor);
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
    var board1 = new HashBoard();
    var board2 = new HashBoard();
    Boards.copyBoard(board).accept(board1);
    Boards.copyBoard(board).accept(board2);

    if (mainColor == currentColor) {
      if (GameStateChecker.isMate(board1, BotUtils.getOtherColor(mainColor))) {
        return getSimpleEstimationForColor(board, mainColor) + 2000;
      }
    } else {
      if (GameStateChecker.isMate(board1, mainColor)) {
        return getSimpleEstimationForColor(board, mainColor) - 2000;
      }
    }

    if (GameStateChecker.isDraw(board2, gameHistory)) {
      return getSimpleEstimationForColor(board, BotUtils.getOtherColor(mainColor));
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
    return board.getAllPieceByColorPosition(color).stream()
        .map(pos -> PRICE_MAP.get(board.getPiece(pos).getFigureType()))
        .reduce(0, Integer::sum);
  }
}
