package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Boards;
import java.util.List;
import java.util.stream.Collectors;

public class GameStateChecker {
  /**
   * Метод проверяет стоит-ли на доске шах определённому цвету фигур.
   *
   * @param board Доска
   * @param color Цвет
   * @return Стоит ли на доске шах
   */
  public static boolean isCheck(Board board, Color color) {
    Position kingPoz = board.getKingPositionByColor(color);

    List<Position> piecePosition = board.getAllPiecePositionByColor(color.getOpposite());
    for (Position position : piecePosition) {
      var piece = board.getPiece(position);
      FigureType promotionPiece = null;
      if (piece.getFigureType() == FigureType.PAWN
          && (kingPoz.row().value() == 0 || kingPoz.row().value() == 7)) {
        promotionPiece = FigureType.QUEEN;
      }

      Move move = new Move(position, kingPoz, promotionPiece);

      if (piece.canMove(board, move, false, true)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Метод проверяет-стоит ли на доске мат определённому цвету фигур.
   *
   * @param board Доска
   * @param color Цвет
   * @return Стоит ли на доске мат
   */
  public static boolean isMate(Board board, Color color) {
    if (!isCheck(board, color)) {
      return false;
    }

    List<Position> piecePosition = board.getAllPiecePositionByColor(color);
    for (Position position : piecePosition) {
      Piece piece = board.getPiece(position);

      for (Move move : piece.getAllMoves(board, position)) {
        Board copyBoard = new HashBoard();
        Boards.copy(board).accept(copyBoard);
        Piece copyPiece = copyBoard.getPiece(position);

        if (copyPiece.move(copyBoard, move)) {
          if (!isCheck(copyBoard, color)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Метод проверяет-стоит ли на доске ничья.
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
    var otherColor = lastMovedPiece.getColor().getOpposite();

    if (isStaleMate(board, otherColor)) {
      return true;
    }

    List<Position> whites = board.getAllPiecePositionByColor(Color.WHITE);
    List<Position> blacks = board.getAllPiecePositionByColor(Color.BLACK);

    List<FigureType> whiteFiguresList =
        whites.stream()
            .map(position -> board.getPiece(position).getFigureType())
            .collect(Collectors.toList());
    List<FigureType> blackFiguresList =
        blacks.stream()
            .map(position -> board.getPiece(position).getFigureType())
            .collect(Collectors.toList());

    return drawWithLackOfFigures(whiteFiguresList, blackFiguresList)
        || drawWithRepeatPosition(gameHistory, board)
        || drawWithGameWithoutTakingAndAdvancingPawns(gameHistory);
  }

  /**
   * Метод проверяет-стоит ли на доске пат определённому виду фигур.
   *
   * <p>Не используем этот метод!!! Используем isDraw(...).
   *
   * @param board Доска
   * @return Стоит ли на доске пат
   */
  public static boolean isStaleMate(Board board, Color color) {
    List<Position> positions = board.getAllPiecePositionByColor(color);
    for (Position position : positions) {
      var piece = board.getPiece(position);

      var allMoves = piece.getAllMoves(board, position);
      if (!allMoves.isEmpty()) {
        for (Move move : allMoves) {
          var copyBoard = new HashBoard();
          Boards.copy(board).accept(copyBoard);
          Piece copyPiece = copyBoard.getPiece(position);

          if (copyPiece.move(copyBoard, move)) {
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
   * Метод проверяет стоит-ли на доске ничья с недостатком материала.
   *
   * @param white Белые фигуры
   * @param black Чёрные фигуры
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithLackOfFigures(List<FigureType> white, List<FigureType> black) {
    if (white.size() == 1 && white.get(0).equals(FigureType.KING)) {
      return drawWithFiguresAgainstKing(black);
    } else if (black.size() == 1 && black.get(0).equals(FigureType.KING)) {
      return drawWithFiguresAgainstKing(white);
    }

    return false;
  }

  /**
   * Метод проверяет стоит-ли на доске ничья с недостатком материала против короля.
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
   * Метод проверяет-стоит ли на доске ничья из-за отсутствия взятий или продвижений пешки на
   * протяжении 75 ходов.
   *
   * @param gameHistory История партии
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithGameWithoutTakingAndAdvancingPawns(GameHistory gameHistory) {
    return gameHistory.getMovesWithoutTakingAndAdvancingPawns() >= 75;
  }

  /**
   * Метод проверяет-стоит ли на доске ничья из-за троекратного повторения позиции.
   *
   * @param gameHistory История партии
   * @return Стоит ли на доске ничья
   */
  private static boolean drawWithRepeatPosition(GameHistory gameHistory, Board board) {
    return gameHistory.getMaxRepeatPosition(board) >= 3;
  }
}
