package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.Piece;
import java.io.OutputStream;
import java.io.PrintStream;

/** Класс, отвечающий за отображение доски. */
public class BoardRender {

  public static void showBoard(OutputStream outputStream, Board board) {
    PrintStream printStream = new PrintStream(outputStream);
    showBoard(printStream, board);
  }

  /**
   * Метод для вывода Board.
   *
   * @param printStream Поток вывода
   * @param board Доска
   */
  public static void showBoard(PrintStream printStream, Board board) {
    printStream.println("┏━━━━━━━━━━━━━━━━━━━━━━┓");
    for (int i = 7; i >= 0; i--) {
      printStream.print("┃ " + (i + 1) + " ");
      for (int j = 0; j < 8; j++) {
        Piece piece = board.getPiece(j, i);
        if (piece != null) {
          printStream.print("│" + pieceSymbol(piece));
        } else {
          printStream.print("│ ");
        }
      }
      printStream.print("│  ┃\n");
    }

    printStream.print("┃    ");
    for (int i = 0; i < 8; i++) {
      printStream.print((char) (97 + i) + " ");
    }

    printStream.println("  ┃");
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
