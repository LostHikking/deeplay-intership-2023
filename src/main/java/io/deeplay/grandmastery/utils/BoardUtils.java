package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import java.util.function.Consumer;

/**
 * Класс {@code BoardUtils} предоставляет утилиты для работы с шахматными досками и фигурами. Он
 * содержит статические методы для создания шахматных досок по умолчанию, досок по правилам фишера и
 * для копирования досок.
 */
public class BoardUtils {

  /**
   * Возвращает одного из наследников {@link Board}, который создает шахматную доску со стандартной
   * расстановкой фигур.
   *
   * @return Наследник {@link Board}, представляющий шахматную доску по умолчанию.
   */
  public static Consumer<Board> defaultChess() {
    return defaultBoard -> {
      for (Color color : Color.values()) {
        int actualRow;

        if (color == Color.WHITE) {
          actualRow = 0;
        } else {
          actualRow = 7;
        }
        for (int j = 0; j < 8; j++) {
          defaultBoard.setPiece(
              new Position(new Column(j), new Row(Math.abs(actualRow - 1))), new Pawn(color));
        }

        defaultBoard.setPiece(new Position(new Column(0), new Row(actualRow)), new Rook(color));
        defaultBoard.setPiece(new Position(new Column(7), new Row(actualRow)), new Rook(color));

        defaultBoard.setPiece(new Position(new Column(1), new Row(actualRow)), new Knight(color));
        defaultBoard.setPiece(new Position(new Column(6), new Row(actualRow)), new Knight(color));

        defaultBoard.setPiece(new Position(new Column(2), new Row(actualRow)), new Bishop(color));
        defaultBoard.setPiece(new Position(new Column(5), new Row(actualRow)), new Bishop(color));

        defaultBoard.setPiece(new Position(new Column(3), new Row(actualRow)), new Queen(color));
        defaultBoard.setPiece(new Position(new Column(4), new Row(actualRow)), new King(color));
      }
    };
  }

  /**
   * Возвращает одного из наследников {@link Board}, который создает шахматную доску, используя
   * расстановку фигур Фишера. Эта расстановка используется для случайного размещения фигур на доске
   * в начале игры.
   *
   * @return Наследник {@link Board}, представляющий настраиваемую доску по расстановке Фишера.
   */
  public static Consumer<Board> fischerChess() {
    return board -> {};
  }

  /**
   * Возвращает одного из наследников {@link Board}, который копирует содержимое исходной шахматной
   * доски в целевую доску.
   *
   * @param sourceBoard исходная шахматная доска, которую необходимо скопировать.
   * @return Наследник {@link Board}, который копирует исходную доску в целевую доску.
   * @throws io.deeplay.grandmastery.exceptions.GameException если sourceBoard равен null.
   */
  public static Consumer<Board> copyBoard(Board sourceBoard) {
    return destinationBoard -> {
      if (sourceBoard == null) {
        throw GameErrorCode.NULL_POINTER_SOURCE_BOARD.asException();
      }

      if (sourceBoard.getLastMove() != null) {
        destinationBoard.setLastMove(copyMove(sourceBoard.getLastMove()));
      }

      Piece piece;
      for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
          piece = sourceBoard.getPiece(col, row);
          if (piece != null) {
            destinationBoard.setPiece(
                new Position(new Column(col), new Row(row)), copyPiece(piece));
          }
        }
      }
    };
  }

  private static Move copyMove(Move move) {
    Position from =
        new Position(new Column(move.from().col().value()), new Row(move.from().row().value()));
    Position to =
        new Position(new Column(move.to().col().value()), new Row(move.to().row().value()));

    return new Move(from, to, move.promotionPiece());
  }

  private static Piece copyPiece(Piece sourcePiece) {
    switch (sourcePiece.getFigureType()) {
      case KING -> {
        return new King(sourcePiece.getColor());
      }
      case PAWN -> {
        return new Pawn(sourcePiece.getColor());
      }
      default -> {
        return null;
      }
    }
  }
}
