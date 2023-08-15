package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Класс {@code BoardUtils} предоставляет утилиты для работы с шахматными досками и фигурами. Он
 * содержит статические методы для создания шахматных досок по умолчанию, досок по правилам фишера и
 * для копирования досок.
 */
public class Boards {

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
    return fischerBoard -> {
      for (int i = 0; i < 8; i++) {
        fischerBoard.setPiece(new Position(new Column(i), new Row(1)), new Pawn(Color.WHITE));
        fischerBoard.setPiece(new Position(new Column(i), new Row(6)), new Pawn(Color.BLACK));
      }
      ArrayList<Integer> positions = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7));
      // ставим короля
      int posKing = (int) (Math.random() * 5) + 1;
      fischerBoard.setPiece(new Position(new Column(posKing), new Row(0)), new King(Color.WHITE));
      fischerBoard.setPiece(new Position(new Column(posKing), new Row(7)), new King(Color.BLACK));
      positions.remove(Integer.valueOf(posKing));
      // ставим ладью по левую сторону
      int posRook1 = (int) (Math.random() * posKing);
      fischerBoard.setPiece(new Position(new Column(posRook1), new Row(0)), new Rook(Color.WHITE));
      fischerBoard.setPiece(new Position(new Column(posRook1), new Row(7)), new Rook(Color.BLACK));
      positions.remove(Integer.valueOf(posRook1));
      // по правую
      int posRook2 = (int) (Math.random() * (7 - posKing)) + posKing + 1;
      fischerBoard.setPiece(new Position(new Column(posRook2), new Row(0)), new Rook(Color.WHITE));
      fischerBoard.setPiece(new Position(new Column(posRook2), new Row(7)), new Rook(Color.BLACK));
      positions.remove(Integer.valueOf(posRook2));
      // ставим первого слона в рандомную клетку из оставшихся
      int ind = (int) (Math.random() * 5);
      int posBishop1 = positions.get(ind);
      fischerBoard.setPiece(
          new Position(new Column(posBishop1), new Row(0)), new Bishop(Color.WHITE));
      fischerBoard.setPiece(
          new Position(new Column(posBishop1), new Row(7)), new Bishop(Color.BLACK));
      positions.remove(Integer.valueOf(posBishop1));
      // ставим второго слона в клетку противоположного цвета
      int posBishop2;
      ind = (int) (Math.random() * 4);
      if (posBishop1 % 2 == 0) {
        while (positions.get(ind) % 2 == 0) {
          ind = (int) (Math.random() * 3);
        }
        posBishop2 = positions.get(ind);
      } else {
        while (positions.get(ind) % 2 != 0) {
          ind = (int) (Math.random() * 3);
        }
        posBishop2 = positions.get(ind);
      }
      fischerBoard.setPiece(
          new Position(new Column(posBishop2), new Row(0)), new Bishop(Color.WHITE));
      fischerBoard.setPiece(
          new Position(new Column(posBishop2), new Row(7)), new Bishop(Color.BLACK));
      positions.remove(Integer.valueOf(posBishop2));
      // ставим рандомно двух коней и ферзя
      ind = (int) (Math.random() * 3);
      int posKnight1 = positions.get(ind);
      fischerBoard.setPiece(
          new Position(new Column(posKnight1), new Row(0)), new Knight(Color.WHITE));
      fischerBoard.setPiece(
          new Position(new Column(posKnight1), new Row(7)), new Knight(Color.BLACK));
      positions.remove(Integer.valueOf(posKnight1));
      ind = (int) (Math.random() * 2);
      int posKnight2 = positions.get(ind);
      fischerBoard.setPiece(
          new Position(new Column(posKnight2), new Row(0)), new Knight(Color.WHITE));
      fischerBoard.setPiece(
          new Position(new Column(posKnight2), new Row(7)), new Knight(Color.BLACK));
      positions.remove(Integer.valueOf(posKnight2));
      int posQueen = positions.get(0);
      fischerBoard.setPiece(new Position(new Column(posQueen), new Row(0)), new Queen(Color.WHITE));
      fischerBoard.setPiece(new Position(new Column(posQueen), new Row(7)), new Queen(Color.BLACK));
    };
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

      destinationBoard.clear();
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
      case ROOK -> {
        return new Rook(sourcePiece.getColor());
      }
      case QUEEN -> {
        return new Queen(sourcePiece.getColor());
      }
      case BISHOP -> {
        return new Bishop(sourcePiece.getColor());
      }
      case KNIGHT -> {
        return new Knight(sourcePiece.getColor());
      }
      default -> throw GameErrorCode.UNKNOWN_FIGURE_TYPE.asException();
    }
  }

  /**
   * Метод возвращает строку из доски.
   *
   * @param board Board
   * @return String
   */
  public static String getStringFromBoard(Board board) {
    var result = new StringBuilder();

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        var piece = board.getPiece(i, j);
        if (piece != null) {
          var symbol = String.valueOf(piece.getFigureType().getSymbol());

          if (piece.getColor() == Color.WHITE) {
            result.append(symbol);
          } else {
            result.append(symbol.toUpperCase(Locale.ROOT));
          }
        } else {
          result.append("_");
        }
      }
    }
    return result.toString();
  }

  /**
   * Метод возвращает доску из строки.
   *
   * @param string String
   * @return Board
   */
  public static Board getBoardFromString(String string) {
    var board = new HashBoard();

    for (int i = 0; i < string.length(); i++) {
      var character = String.valueOf(string.charAt(i));
      if (!character.equals("_")) {
        var color =
            character.equals(character.toLowerCase(Locale.ROOT)) ? Color.WHITE : Color.BLACK;
        var piece =
            Objects.requireNonNull(
                    Arrays.stream(FigureType.values())
                        .filter(
                            type ->
                                String.valueOf(type.getSymbol())
                                    .equals(character.toLowerCase(Locale.ROOT)))
                        .findAny()
                        .orElse(null))
                .getPiece(color);
        board.setPiece(new Position(new Column(i / 8), new Row(i % 8)), piece);
      }
    }

    return board;
  }

  /**
   * Функция проверяет равны ли доски.
   *
   * @param first Первая доска
   * @param second Вторая доска
   * @return Равны ли доски
   */
  public static boolean isEqualsBoards(Board first, Board second) {
    List<Position> allOldPosition = first.getAllPiecePosition();

    for (Position position : allOldPosition) {
      Piece historyBoardPiece = first.getPiece(position);
      Piece boardPiece = second.getPiece(position);
      if (historyBoardPiece != null && !historyBoardPiece.equals(boardPiece)) {
        return false;
      } else if (boardPiece != null && !boardPiece.equals(historyBoardPiece)) {
        return false;
      }
    }

    return true;
  }
}
