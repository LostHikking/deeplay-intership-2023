package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/** Класс, отвечающий за отображение доски. */
public class BoardRender {
  /**
   * Метод для вывода HashBoard.
   *
   * @param outputStream Поток вывода
   * @param hashBoard Доска
   */
  public static void showHashBoard(OutputStream outputStream, HashBoard hashBoard) {
    Charset utf8 = Charset.forName("UTF-8");
    Writer writer = new OutputStreamWriter(outputStream, utf8);
    PrintWriter printWriter = new PrintWriter(writer);

    for (int i = 0; i < 8; i++) {
      printWriter.print("+-----------------------+\n");
      for (int j = 0; j < 8; j++) {
        Position positionToCheck = new Position(new Column(j), new Row(i));
        Piece piece = hashBoard.getPieces().get(positionToCheck);

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
