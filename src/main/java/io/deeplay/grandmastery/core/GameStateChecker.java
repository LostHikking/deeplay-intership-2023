package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.utils.BoardUtils;
import java.util.ArrayList;
import java.util.List;

public class GameStateChecker {
  /**
   * Метод проверяет стоит ли на доске шах определённому цвету фигур.
   *
   * @param board Доска
   * @param color Цвет
   * @return Стоит ли на доске шах
   */
  public static boolean isCheck(Board board, Color color) {
    var kingPoz =
        color == Color.WHITE ? board.getWhiteKingPosition() : board.getBlackKingPosition();

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        var piece = board.getPiece(i, j);
        var move = new Move(new Position(new Column(i), new Row(j)), kingPoz, null);

        if (piece != null && piece.getColor() != color && piece.canMove(board, move, false)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Метод проверяет стоит ли на доске мат определённому цвету фигур.
   *
   * @param board Доска
   * @param color Цвет
   * @return Стоит ли на доске мат
   */
  public static boolean isMate(Board board, Color color) {
    if (!isCheck(board, color)) {
      return false;
    }

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        var piece = board.getPiece(i, j);
        var pos = new Position(new Column(i), new Row(j));

        if (piece != null && piece.getColor() == color) {
          for (Move move : piece.getAllMoves(board, pos)) {
            var copyBoard = new HashBoard();
            BoardUtils.copyBoard(board).accept(copyBoard);

            piece.move(copyBoard, move);
            if (!isCheck(copyBoard, color)) {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

  /**
   * Метод проверяет стоит ли на доске пат определённому виду фигур.
   *
   * @param board Доска
   * @return Стоит ли на доске пат
   */
  private static boolean isStaleMate(Board board, Color color) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        var piece = board.getPiece(i, j);
        var pos = new Position(new Column(i), new Row(j));

        if (piece != null && piece.getColor() == color) {
          var allMoves = piece.getAllMoves(board, pos);
          if (!allMoves.isEmpty()) {
            for (Move move : allMoves) {
              var copyBoard = new HashBoard();
              BoardUtils.copyBoard(board).accept(copyBoard);

              piece.move(copyBoard, move);
              if (!isCheck(copyBoard, color)) {
                return false;
              }
            }
          }
        }
      }
    }

    return true;
  }

  /**
   * Метод проверяет стоит ли на доске ничья.
   *
   * @param board Доска
   * @return Стоит ли на доске ничья
   */
  public static boolean isDraw(Board board, GameHistory gameHistory) {
    var lastMove = board.getLastMove();

    if (lastMove == null) {
      return false;
    }

    var lastMovedPiece = board.getPiece(lastMove.to());
    var otherColor = lastMovedPiece.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE;

    if (isStaleMate(board, otherColor)) {
      return true;
    }

    var whiteFiguresList = new ArrayList<FigureType>();
    var blackFiguresList = new ArrayList<FigureType>();

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        var piece = board.getPiece(i, j);
        if (piece != null) {
          if (piece.getColor() == Color.WHITE) {
            whiteFiguresList.add(piece.getFigureType());
          } else {
            blackFiguresList.add(piece.getFigureType());
          }
        }
      }
    }

    return drawWithLackOfFigures(whiteFiguresList, blackFiguresList)
        && drawWithRepeatPosition(gameHistory, board)
        && drawWithGameWithoutTakingAndAdvancingPawns(gameHistory);
  }

  /**
   * Метод проверяет стоит ли на доске ничья с недостатком материала.
   *
   * @param white Белые фигуры
   * @param black Чёрные фигуры
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithLackOfFigures(List<FigureType> white, List<FigureType> black) {
    if (white.size() == 1 && white.get(0).equals(FigureType.KING)) {
      return drawWithFiguresAgainstKing(white);
    } else if (black.size() == 1 && black.get(0).equals(FigureType.KING)) {
      return drawWithFiguresAgainstKing(black);
    }

    return false;
  }

  /**
   * Метод проверяет стоит ли на доске ничья с недостатком материала против короля.
   *
   * @param figureTypeList Список фигур против короля
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithFiguresAgainstKing(List<FigureType> figureTypeList) {
    if (figureTypeList.size() == 1) {
      return true;
    }

    figureTypeList.remove(FigureType.KING);

    if (figureTypeList.size() == 1) {
      return figureTypeList.stream()
          .noneMatch(
              figureType -> figureType != FigureType.KNIGHT && figureType != FigureType.BISHOP);
    } else if (figureTypeList.size() == 2) {
      return figureTypeList.stream().noneMatch(figureType -> figureType != FigureType.KNIGHT);
    }

    return false;
  }

  /**
   * Метод проверяет стоит ли на доске ничья из-за отсутствия взятий или продвижений пешки на
   * протяжении 75 ходов.
   *
   * @param gameHistory История партии
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithGameWithoutTakingAndAdvancingPawns(GameHistory gameHistory) {
    return gameHistory.getMovesWithoutTakingAndAdvancingPawns() >= 75;
  }

  /**
   * Метод проверяет стоит ли на доске ничья из-за 5-ти кратного повторения позиции.
   *
   * @param gameHistory История партии
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithRepeatPosition(GameHistory gameHistory, Board board) {
    return gameHistory.getMaxRepeatPosition(board) >= 5;
  }
}
