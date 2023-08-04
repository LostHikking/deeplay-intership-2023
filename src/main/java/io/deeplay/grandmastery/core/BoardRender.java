package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.figures.Piece;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** Класс, отвечающий за отображение доски. */
public class BoardRender {
  /**
   * Метод для вывода Board.
   *
   * @param outputStream Поток вывода
   * @param board Доска
   */
  public static void showBoard(OutputStream outputStream, Board board) {
    Charset utf8 = StandardCharsets.UTF_8;
    Writer writer = new OutputStreamWriter(outputStream, utf8);
    PrintWriter printWriter = new PrintWriter(writer);

    for (int i = 0; i < 8; i++) {
      printWriter.print("+-----------------------+\n");
      for (int j = 0; j < 8; j++) {
        Piece piece = board.getPiece(j, i);
        if (piece != null) {
          printWriter.print("|" + piece.getSymbol());
        } else {
          printWriter.print("|  ");
        }
      }
      printWriter.print("|\n");
    }

    printWriter.print("+-----------------------+\n");
    printWriter.flush();
    printWriter.close();
  }
}
