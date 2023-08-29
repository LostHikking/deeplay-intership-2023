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
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
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
        int row = color == Color.WHITE ? 0 : 7;
        setPawnsByRow(defaultBoard, row, color);

        defaultBoard.setPiece(new Position(new Column(0), new Row(row)), new Rook(color));
        defaultBoard.setPiece(new Position(new Column(7), new Row(row)), new Rook(color));

        defaultBoard.setPiece(new Position(new Column(1), new Row(row)), new Knight(color));
        defaultBoard.setPiece(new Position(new Column(6), new Row(row)), new Knight(color));

        defaultBoard.setPiece(new Position(new Column(2), new Row(row)), new Bishop(color));
        defaultBoard.setPiece(new Position(new Column(5), new Row(row)), new Bishop(color));

        defaultBoard.setPiece(new Position(new Column(3), new Row(row)), new Queen(color));
        defaultBoard.setPiece(new Position(new Column(4), new Row(row)), new King(color));
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
      List<Integer> positions = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7));
      Random random = new Random();

      int blackBishop = positions.get(random.nextInt(4)) * 2;
      int whiteBishop = positions.get(random.nextInt(4)) * 2 + 1;
      positions.remove(Integer.valueOf(blackBishop));
      positions.remove(Integer.valueOf(whiteBishop));

      int queen = positions.get(random.nextInt(positions.size()));
      positions.remove(Integer.valueOf(queen));
      int firstKnight = positions.get(random.nextInt(positions.size()));
      positions.remove(Integer.valueOf(firstKnight));
      int secondKnight = positions.get(random.nextInt(positions.size()));
      positions.remove(Integer.valueOf(secondKnight));

      Collections.sort(positions);
      int leftRook = positions.get(0);
      int king = positions.get(1);
      int rightRook = positions.get(2);

      for (Color color : Color.values()) {
        int row = color == Color.WHITE ? 0 : 7;
        setPawnsByRow(fischerBoard, row, color);

        fischerBoard.setPiece(new Position(new Column(king), new Row(row)), new King(color));
        fischerBoard.setPiece(new Position(new Column(queen), new Row(row)), new Queen(color));

        fischerBoard.setPiece(new Position(new Column(leftRook), new Row(row)), new Rook(color));
        fischerBoard.setPiece(new Position(new Column(rightRook), new Row(row)), new Rook(color));

        fischerBoard.setPiece(
            new Position(new Column(firstKnight), new Row(row)), new Knight(color));
        fischerBoard.setPiece(
            new Position(new Column(secondKnight), new Row(row)), new Knight(color));

        fischerBoard.setPiece(
            new Position(new Column(blackBishop), new Row(row)), new Bishop(color));
        fischerBoard.setPiece(
            new Position(new Column(whiteBishop), new Row(row)), new Bishop(color));
      }
    };
  }

  private static void setPawnsByRow(Board board, int row, Color color) {
    for (int j = 0; j < 8; j++) {
      board.setPiece(new Position(new Column(j), new Row(Math.abs(row - 1))), new Pawn(color));
    }
  }

  /**
   * Возвращает одного из наследников {@link Board}, который копирует содержимое исходной шахматной
   * доски в целевую доску.
   *
   * @param sourceBoard исходная шахматная доска, которую необходимо скопировать.
   * @return Наследник {@link Board}, который копирует исходную доску в целевую доску.
   * @throws GameException если sourceBoard равен null.
   */
  public static Consumer<Board> copyBoard(Board sourceBoard) throws GameException {
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

  private static Piece copyPiece(Piece sourcePiece) throws GameException {
    switch (sourcePiece.getFigureType()) {
      case KING -> {
        Piece king = new King(sourcePiece.getColor());
        king.setMoved(sourcePiece.isMoved());
        return king;
      }
      case PAWN -> {
        Piece pawn = new Pawn(sourcePiece.getColor());
        pawn.setMoved(sourcePiece.isMoved());
        return pawn;
      }
      case ROOK -> {
        Piece rook = new Rook(sourcePiece.getColor());
        rook.setMoved(sourcePiece.isMoved());
        return rook;
      }
      case QUEEN -> {
        Queen queen = new Queen(sourcePiece.getColor());
        queen.setMoved(sourcePiece.isMoved());
        return queen;
      }
      case BISHOP -> {
        Bishop bishop = new Bishop(sourcePiece.getColor());
        bishop.setMoved(sourcePiece.isMoved());
        return bishop;
      }
      case KNIGHT -> {
        Knight knight = new Knight(sourcePiece.getColor());
        knight.setMoved(sourcePiece.isMoved());
        return knight;
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
