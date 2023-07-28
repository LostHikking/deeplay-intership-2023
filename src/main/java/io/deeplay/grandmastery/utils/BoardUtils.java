package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import java.util.function.Consumer;

/**
 * Класс {@code BoardUtils} предоставляет утилиты для работы с шахматными досками и фигурами. Он
 * содержит статические методы для создания шахматных досок по умолчанию, досок по правилам фишера и
 * для копирования досок.
 */
public class BoardUtils {

  /**
   * Возвращает одного из наследников {@link Board}, который создает шахматную доску по умолчанию
   * без начальных фигур.
   *
   * @return Наследник {@link Board}, представляющий шахматную доску по умолчанию.
   */
  public static Consumer<Board> defaultChess() {
    return board -> {};
  }

  /**
   * Возвращает одного из наследников {@link Board}, который создает настраиваемую шахматную доску,
   * используя расстановку фигур Фишера. Эта расстановка используется для случайного размещения
   * фигур на доске в начале игры.
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

  /**
   * Создает копию объекта {@link Move}.
   *
   * @param move исходный ход, который необходимо скопировать.
   * @return новый объект класса {@link Move}, являющейся копией исходного объекта.
   */
  private static Move copyMove(Move move) {
    Position from =
        new Position(new Column(move.from().col().value()), new Row(move.from().row().value()));
    Position to =
        new Position(new Column(move.to().col().value()), new Row(move.to().row().value()));

    return new Move(from, to, move.promotionPiece());
  }

  /**
   * Создает копию объекта {@link Piece}.
   *
   * @param sourcePiece исходная фигура, которую необходимо скопировать.
   * @return новый экземпляр фигуры {@link Piece}, являющейся копией исходной фигуры.
   */
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
