package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Класс-утилита для ботов. */
public class BotUtils {
  /**
   * Функция возвращает противоположный цвет.
   *
   * @param color Цвет
   * @return Противоположный цвет
   */
  public static Color getOtherColor(Color color) {
    if (color == Color.WHITE) {
      return Color.BLACK;
    }
    return Color.WHITE;
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
    var positions = board.getAllPiecePositionByColor(color);

    for (Position position : positions) {
      possibleMove.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return possibleMove;
  }

  /**
   * Функция возвращает новую доску, после хода, не меняя передаваемую доску.
   *
   * @param board Доска
   * @param move Ход
   * @return Новая доска
   */
  public static Board getCopyBoardAfterMove(Move move, Board board) {
    var tempBoard = new HashBoard();
    Boards.copy(board).accept(tempBoard);
    var piece = tempBoard.getPiece(move.from());
    piece.move(tempBoard, move);

    return tempBoard;
  }

  /**
   * Функция создаёт запись в формате нотации Форсайта-Эдвардса (FEN) по доске.
   *
   * @param board Доска
   * @param currentColor Чей сейчас ход
   * @param gameHistory История партии
   * @return Нотация
   */
  public static String getFenFromBoard(Board board, Color currentColor, GameHistory gameHistory) {
    var result = new StringBuilder();

    writeFenBoard(result, board);

    if (currentColor == Color.WHITE) {
      result.append(" w");
    } else {
      result.append(" b");
    }

    var buffer = new StringBuilder(" ");

    if (isUnmovedKingOrRook(board.getPiece(board.getWhiteKingPosition()))) {
      if (isUnmovedKingOrRook(board.getPiece(Position.fromString("h1")))) {
        buffer.append("K");
      }
      if (isUnmovedKingOrRook(board.getPiece(Position.fromString("a1")))) {
        buffer.append("Q");
      }
    }

    if (isUnmovedKingOrRook(board.getPiece(board.getBlackKingPosition()))) {
      if (isUnmovedKingOrRook(board.getPiece(Position.fromString("h8")))) {
        buffer.append("k");
      }

      if (isUnmovedKingOrRook(board.getPiece(Position.fromString("a8")))) {
        buffer.append("q");
      }
    }

    if (buffer.length() == 1) {
      buffer.append("-");
    }

    result.append(buffer);

    var lastMove = board.getLastMove();
    if (lastMove != null) {
      var piece = board.getPiece(lastMove.to());
      if (piece.getFigureType() == FigureType.PAWN) {
        if (Math.abs(lastMove.from().row().value() - lastMove.to().row().value()) > 1) {
          result.append(" ");
          if (currentColor == Color.WHITE) {
            result.append(
                Position.getString(
                    new Position(lastMove.to().col(), new Row(lastMove.to().row().value() + 1))));
          } else {
            result.append(
                Position.getString(
                    new Position(lastMove.to().col(), new Row(lastMove.to().row().value() - 1))));
          }
          result.append(" ");
        } else {
          result.append(" - ");
        }
      } else {
        result.append(" - ");
      }
    } else {
      result.append(" - ");
    }

    result.append(gameHistory.getMovesWithoutTakingAndAdvancingPawns()).append(" ");
    result.append((gameHistory.getMoves().size() / 2) + 1);

    return result.toString();
  }

  /**
   * Метод записывает в StringBuilder доску в FEN формате.
   *
   * @param result StringBuilder
   * @param board Доска
   */
  public static void writeFenBoard(StringBuilder result, Board board) {
    for (int i = 7; i >= 0; i--) {
      for (int j = 0; j < 8; j++) {
        var piece = board.getPiece(j, i);
        if (piece != null) {
          var symbol = String.valueOf(piece.getFigureType().getSymbol());

          if (piece.getColor() == Color.BLACK) {
            result.append(symbol);
          } else {
            result.append(symbol.toUpperCase(Locale.ROOT));
          }
        } else {
          if (result.isEmpty()) {
            result.append("1");
          } else {
            var prev = String.valueOf(result.charAt(result.length() - 1));
            try {
              var num = Integer.parseInt(prev);
              result.deleteCharAt(result.length() - 1);
              result.append(num + 1);
            } catch (NumberFormatException e) {
              result.append("1");
            }
          }
        }
      }
      if (i != 0) {
        result.append("/");
      }
    }
  }

  private static boolean isUnmovedKingOrRook(Piece piece) {
    return piece != null
        && (piece.getFigureType() == FigureType.KING || piece.getFigureType() == FigureType.ROOK)
        && !piece.isMoved();
  }
}
