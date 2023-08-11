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
    printStream.println("┏━━━━━━━━━━━━━━━━━━━━━━┓");
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
      printStream.print(color == Color.WHITE ? "┃ " + (i + 1) + " " : "┃  ");
      for (int j = 0; j < 8; j++) {
        int horizontalIndex = (color == Color.WHITE) ? j : 7 - j;
        Piece piece = board.getPiece(horizontalIndex, i);
        if (piece != null) {
          printStream.print("│" + pieceSymbol(piece));
        } else {
          printStream.print("│ ");
        }
      }
      printStream.print(color == Color.WHITE ? "│  ┃\n" : "│ " + (i + 1) + " ┃\n");
    }

    printStream.print(color == Color.WHITE ? "┃    " : "┃   ");
    for (int i = 0; i < 8; i++) {
      printStream.print(color == Color.WHITE ? (char) (97 + i) + " " : (char) (104 - i) + " ");
    }

    printStream.println(color == Color.WHITE ? "  ┃" : "   ┃");
    printStream.println("┗━━━━━━━━━━━━━━━━━━━━━━┛");
    printStream.flush();
  }

  private static char pieceSymbol(Piece piece) {
    switch (piece.getFigureType()) {
      case KING -> {
        return (piece.getColor() == Color.WHITE) ? '♚' : '♔';
      }
      case QUEEN -> {
        return (piece.getColor() == Color.WHITE) ? '♛' : '♕';
      }
      case ROOK -> {
        return (piece.getColor() == Color.WHITE) ? '♜' : '♖';
      }
      case BISHOP -> {
        return (piece.getColor() == Color.WHITE) ? '♝' : '♗';
      }
      case KNIGHT -> {
        return (piece.getColor() == Color.WHITE) ? '♞' : '♘';
      }
      case PAWN -> {
        return (piece.getColor() == Color.WHITE) ? '♟' : '♙';
      }
      default -> throw GameErrorCode.INCORRECT_FIGURE_CHARACTER.asException();
    }
  }
}
