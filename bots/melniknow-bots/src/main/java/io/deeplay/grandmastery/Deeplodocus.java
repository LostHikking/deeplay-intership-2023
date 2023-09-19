package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.BotUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Deeplodocus extends Player {
  public record MovesAndEst(List<Move> moves, Integer est) {}

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

  private static final String modelFileName = "1_400_000pos_400ep.h5";
  private static final MultiLayerNetwork multiLayerNetwork;

  static {
    try {
      var stream =
          Objects.requireNonNull(
              Deeplodocus.class.getClassLoader().getResourceAsStream(modelFileName));
      multiLayerNetwork = KerasModelImport.importKerasSequentialModelAndWeights(stream);
    } catch (IOException
        | InvalidKerasConfigurationException
        | UnsupportedKerasConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Конструктор с параметрами.
   *
   * @param color Цвет
   */
  public Deeplodocus(Color color) {
    super("Deeplodocus", color);
  }

  @Override
  public Move createMove() throws GameException {
    var optimalOutput = getColor() == Color.WHITE ? 0d : 1d;
    var board = getBoard();

    Move resultMove = null;
    var moves =
        getBestMovesByMinimax(
                3,
                getBoard(),
                color,
                lastMove,
                color,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                gameHistory,
                BotUtils.getPossibleMoves(getBoard(), color),
                true)
            .moves;

    for (var move : moves) {
      var tempBoard = new HashBoard();
      Boards.copy(board).accept(tempBoard);

      var piece = tempBoard.getPiece(move.from());
      piece.move(tempBoard, move);

      var out = getEvaluateByBoard(tempBoard);

      if (out > optimalOutput && getColor() == Color.WHITE) {
        optimalOutput = out;
        resultMove = move;
      }

      if (out < optimalOutput && getColor() == Color.BLACK) {
        optimalOutput = out;
        resultMove = move;
      }
    }
    this.setLastMove(resultMove);

    return resultMove;
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }

  /**
   * Функция возвращает оценку нейросети по доске.
   *
   * @param board Доска
   * @return Оценка нейросети [0,1]
   */
  public static double getEvaluateByBoard(Board board) {
    try (var input = getNdArrayFromBoard(board)) {
      return multiLayerNetwork.output(input).toDoubleMatrix()[0][0];
    }
  }

  /**
   * Переводим доску в формат, понятный нейросети.
   *
   * @param board Доска
   * @return Массив (1, 768)
   */
  public static INDArray getNdArrayFromBoard(Board board) {
    var allWhitePositions = board.getAllPiecePositionByColor(Color.WHITE);
    var allBlackPositions = board.getAllPiecePositionByColor(Color.BLACK);

    return Nd4j.concat(
        1,
        getNdArrayByFigureType(board, allWhitePositions, FigureType.PAWN, Color.WHITE),
        getNdArrayByFigureType(board, allWhitePositions, FigureType.ROOK, Color.WHITE),
        getNdArrayByFigureType(board, allWhitePositions, FigureType.KNIGHT, Color.WHITE),
        getNdArrayByFigureType(board, allWhitePositions, FigureType.BISHOP, Color.WHITE),
        getNdArrayByFigureType(board, allWhitePositions, FigureType.QUEEN, Color.WHITE),
        getNdArrayByFigureType(board, allWhitePositions, FigureType.KING, Color.WHITE),
        getNdArrayByFigureType(board, allBlackPositions, FigureType.PAWN, Color.BLACK),
        getNdArrayByFigureType(board, allBlackPositions, FigureType.ROOK, Color.BLACK),
        getNdArrayByFigureType(board, allBlackPositions, FigureType.KNIGHT, Color.BLACK),
        getNdArrayByFigureType(board, allBlackPositions, FigureType.BISHOP, Color.BLACK),
        getNdArrayByFigureType(board, allBlackPositions, FigureType.QUEEN, Color.BLACK),
        getNdArrayByFigureType(board, allBlackPositions, FigureType.KING, Color.BLACK));
  }

  /**
   * Возвращаем массив для определённого типа и цвета фигуры.
   *
   * @param board Доска
   * @param allPos Все позиции фигур для данного цвета
   * @param figureType Тип фигуры
   * @param color Цвет
   * @return Массив (1, 64)
   */
  public static INDArray getNdArrayByFigureType(
      Board board, List<Position> allPos, FigureType figureType, Color color) {
    var arr = Nd4j.zeros(1, 64);

    for (var position : allPos) {
      var piece = board.getPiece(position);
      if (piece.getFigureType() == figureType) {
        var col = position.row().value() * 8 + position.col().value();
        arr.putScalar(0, col, color == Color.WHITE ? 1d : -1d);
      }
    }

    return arr;
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

  /**
   * Возвращает лучшие ходы по мнению минимакса.
   *
   * @param deep Глубина
   * @param board Доска
   * @param currentColor Цвет, который ходит
   * @param lastMove Последний сделанный на доске ход
   * @param mainColor Цвет, для которого считается оценка
   * @param alpha Альфа
   * @param beta Бета
   * @param gameHistory История партии
   * @param possibleMove Доступные ходы
   * @param returnMoves Возвращать ли список ходов
   * @return Список лучших ходов с оценкой
   */
  public MovesAndEst getBestMovesByMinimax(
      int deep,
      Board board,
      Color currentColor,
      Move lastMove,
      Color mainColor,
      int alpha,
      int beta,
      GameHistory gameHistory,
      List<Move> possibleMove,
      boolean returnMoves) {

    var isDraw = GameStateChecker.isDraw(board, gameHistory);
    var isMate = GameStateChecker.isMate(board, currentColor);

    if (deep == 0 || isMate || isDraw) {
      return new MovesAndEst(
          new ArrayList<>(List.of(lastMove)),
          getEstimationForBoard(board, mainColor, isDraw, isMate, currentColor));
    }

    var isMax = currentColor == mainColor;
    var moveAndEst =
        new MovesAndEst(new ArrayList<>(List.of()), isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE);

    if (possibleMove.isEmpty()) {
      return moveAndEst;
    }
    for (var move : possibleMove) {
      var tempBoard = BotUtils.getCopyBoardAfterMove(move, board);
      tempBoard.setLastMove(move);

      var tempGameHistory = gameHistory.getCopy();
      tempGameHistory.addBoard(tempBoard);
      tempGameHistory.makeMove(move);

      var recursiveValue =
          getBestMovesByMinimax(
              deep - 1,
              tempBoard,
              currentColor.getOpposite(),
              move,
              mainColor,
              alpha,
              beta,
              tempGameHistory,
              BotUtils.getPossibleMoves(tempBoard, currentColor.getOpposite()),
              false);

      if (isMax && !returnMoves) {
        alpha = Math.max(alpha, recursiveValue.est);
      }

      if (!isMax && !returnMoves) {
        beta = Math.min(beta, recursiveValue.est);
      }

      moveAndEst = getMoveEndEst(isMax, moveAndEst, recursiveValue, move, returnMoves);

      if (!returnMoves && beta <= alpha) {
        break;
      }
    }
    return moveAndEst;
  }

  /**
   * Возвращает ход / набор ходов и оценку.
   *
   * @param isMax Максимизировать ли значение
   * @param moveAndEst Текущее значение
   * @param recursiveValue Значение полученное из рекурсии
   * @param move Ход
   * @param returnMoves Возвращать ли набор ходов
   * @return Ход / набор ходов и оценку
   */
  private MovesAndEst getMoveEndEst(
      boolean isMax,
      MovesAndEst moveAndEst,
      MovesAndEst recursiveValue,
      Move move,
      boolean returnMoves) {
    boolean flag = returnMoves && moveAndEst.est.equals(recursiveValue.est);
    var res = new MovesAndEst(moveAndEst.moves, moveAndEst.est);
    if (isMax) {
      if (moveAndEst.est < recursiveValue.est) {
        res = new MovesAndEst(new ArrayList<>(List.of(move)), recursiveValue.est);
      } else if (flag) {
        res.moves.add(move);
      }
    } else {
      if (moveAndEst.est > recursiveValue.est) {
        res = new MovesAndEst(new ArrayList<>(List.of(move)), recursiveValue.est);
      } else if (flag) {
        res.moves.add(move);
      }
    }

    return res;
  }
}
