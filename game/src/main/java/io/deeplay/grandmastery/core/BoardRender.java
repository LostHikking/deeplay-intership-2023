package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.Piece;
import java.io.OutputStream;
import java.io.PrintStream;

/** Класс, отвечающий за отображение доски. */
public class BoardRender {

  public static void showBoard(OutputStream outputStream, Board board, Color color) {
    PrintStream printStream = new PrintStream(outputStream);
    showBoard(printStream, board, color);
  }

  /**
   * Метод для вывода Board.
   *
   * @param printStream Поток вывода
   * @param board Доска
   * @param color цвет игрока
   */
  public static void showBoard(PrintStream printStream, Board board, Color color) {
    String space = " ";
    int startIndex;
    int endIndex;
    int direction;

    if (color == Color.WHITE) {
      startIndex = 7;
      endIndex = 0;
      direction = -1;
    } else {
      startIndex = 0;
      endIndex = 7;
      direction = 1;
    }

    for (int i = startIndex; i != endIndex + direction; i += direction) {
      printStream.print(color == Color.WHITE ? (i + 1) + space : space + " ");
      for (int j = 0; j < 8; j++) {
        int horizontalIndex = (color == Color.WHITE) ? j : 7 - j;
        Piece piece = board.getPiece(horizontalIndex, i);
        if (piece != null) {
          printStream.print("┃" + pieceSymbol(piece));
        } else {
          printStream.print("┃" + space);
        }
      }
      printStream.print(
          color == Color.WHITE
              ? "┃\n"
              : "┃" + space + (i + 1) + "\n");
    }

    printStream.print(" " + space.repeat(2));
    for (int i = 0; i < 8; i++) {
      printStream.print(color == Color.WHITE ? (char) (97 + i) + space : (char) (104 - i) + space);
    }
    printStream.println();
    printStream.flush();
  }

  private static char pieceSymbol(Piece piece) {
    switch (piece.getFigureType()) {
      case KING -> {
        return (char) ((piece.getColor() == Color.WHITE) ? 0x265A : 0x2654);
      }
      case QUEEN -> {
        return (char) ((piece.getColor() == Color.WHITE) ? 0x265B : 0x2655);
      }
      case ROOK -> {
        return (char) ((piece.getColor() == Color.WHITE) ? 0x265C : 0x2656);
      }
      case BISHOP -> {
        return (char) ((piece.getColor() == Color.WHITE) ? 0x265D : 0x2657);
      }
      case KNIGHT -> {
        return (char) ((piece.getColor() == Color.WHITE) ? 0x265E : 0x2658);
      }
      case PAWN -> {
        return (char) ((piece.getColor() == Color.WHITE) ? 0x265F : 0x2659);
      }
      default -> throw GameErrorCode.INCORRECT_FIGURE_CHARACTER.asException();
    }
  }
}
