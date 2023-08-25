package io.deeplay.grandmastery.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Column;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.core.Row;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class BoardsTest {
  private Board board;

  @BeforeEach
  void init() {
    board = new HashBoard();
  }

  @Test
  public void copyBoardTest() {
    Position position = new Position(new Column(1), new Row(4));
    Piece pawn = new Pawn(Color.WHITE);
    board.setPiece(position, pawn);

    Position whiteKingPosition = new Position(new Column(1), new Row(2));
    Piece whiteKing = new King(Color.WHITE);
    board.setPiece(whiteKingPosition, whiteKing);

    Position blackKingPosition = new Position(new Column(1), new Row(7));
    Piece blackKing = new King(Color.BLACK);
    board.setPiece(blackKingPosition, blackKing);

    Position from = new Position(new Column(1), new Row(2));
    Position to = new Position(new Column(2), new Row(3));
    Move lastMove = new Move(from, to, null);
    board.setLastMove(lastMove);

    Board copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);

    Assertions.assertAll(
        () -> assertNotSame(board, copyBoard, "Refer to different objects"),
        () -> assertEquals(board.getLastMove(), copyBoard.getLastMove(), "Compare last move"),
        () ->
            assertEquals(
                board.getPiece(position), copyBoard.getPiece(position), "Compare piece pawn"),
        () ->
            assertEquals(
                board.getBlackKingPosition(),
                copyBoard.getBlackKingPosition(),
                "Compare black king position"),
        () ->
            assertEquals(
                board.getWhiteKingPosition(),
                copyBoard.getWhiteKingPosition(),
                "Compare white king position"),
        () -> {
          board.removePiece(position);
          assertNotNull(copyBoard.getPiece(position), "After removing the pawn in the hashBoard");
        });
  }

  @Test
  public void copyEmptyBoardTest() {
    Board copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);

    Assertions.assertAll(
        () -> assertNotSame(board, copyBoard, "Refer to different objects"),
        () -> assertNull(copyBoard.getBlackKingPosition(), "Black king position is null"),
        () -> assertNull(copyBoard.getWhiteKingPosition(), "White king position is null"),
        () -> assertNull(copyBoard.getLastMove(), "Last move is null"));
  }

  @Test
  public void copyNullSourceBoardTest() {
    Board copyBoard = new HashBoard();
    assertThrows(GameException.class, () -> Boards.copy(null).accept(copyBoard));
  }

  @Test
  void equalsTwoClassicBoards() {
    Boards.defaultChess().accept(board);

    var string = Boards.getString(board);
    var newBoard = Boards.fromString(string);

    Assertions.assertTrue(Boards.equals(board, newBoard));
  }

  @Test
  void equalsTwoFisherBoards() {
    Boards.fischerChess().accept(board);

    var string = Boards.getString(board);
    var newBoard = Boards.fromString(string);

    Assertions.assertTrue(Boards.equals(board, newBoard));
  }

  @Test
  void notEqualsBoards() {
    Boards.fischerChess().accept(board);

    var newBoard = new HashBoard();
    Boards.fischerChess().accept(newBoard);

    Assertions.assertFalse(Boards.equals(board, newBoard));
  }

  /**
   * Параметризованный тест для проверки начальной расстановки пешек на доске. Тест будет выполнен
   * дважды для строки 1 (белые пешки) и строки 6 (черные пешки).
   *
   * @param row Номер строки для проверки начальной расстановки пешек.
   */
  @ParameterizedTest
  @ValueSource(ints = {1, 6})
  public void startingPawnPositionTest(int row) {
    Boards.defaultChess().accept(board);
    for (int i = 0; i < 8; i++) {
      Piece piece = board.getPiece(i, row);
      Color actualColor;
      if (row == 1) {
        actualColor = Color.WHITE;
      } else {
        actualColor = Color.BLACK;
      }

      Assertions.assertAll(
          actualColor + " pawn check",
          () -> assertEquals(FigureType.PAWN, piece.getFigureType()),
          () -> assertEquals(actualColor, piece.getColor()));
    }
  }

  /**
   * Параметризованный тест для проверки начальной расстановки всех фигур, кроме пешек. Тест будет
   * выполнен 8 раз, для каждого столбца, с проверкой на белую (строка 0) и черную (строка 7)
   * фигуру.
   *
   * @param col Номер столбца для проверки начальной расстановки фигуры (кроме пешек).
   * @param figureType Тип фигуры, ожидаемый на данной позиции.
   */
  @ParameterizedTest
  @MethodSource("getColAndFigureType")
  public void startingPieceExceptPawnTest(int col, FigureType figureType) {
    Boards.defaultChess().accept(board);
    Piece whitePiece = board.getPiece(col, 0);
    Piece blackPiece = board.getPiece(col, 7);

    Assertions.assertAll(
        () -> assertEquals(figureType, whitePiece.getFigureType()),
        () -> assertEquals(figureType, blackPiece.getFigureType()),
        () -> assertEquals(Color.BLACK, blackPiece.getColor()),
        () -> assertEquals(Color.WHITE, whitePiece.getColor()));
  }

  private static Stream<Arguments> getColAndFigureType() {
    return Stream.of(
        Arguments.of(0, FigureType.ROOK),
        Arguments.of(1, FigureType.KNIGHT),
        Arguments.of(2, FigureType.BISHOP),
        Arguments.of(3, FigureType.QUEEN),
        Arguments.of(4, FigureType.KING),
        Arguments.of(5, FigureType.BISHOP),
        Arguments.of(6, FigureType.KNIGHT),
        Arguments.of(7, FigureType.ROOK));
  }

  /** Тест проверяет конфигурацию шахматной доски по расстановке фигур Фишера. */
  @RepeatedTest(value = 100, name = RepeatedTest.LONG_DISPLAY_NAME)
  public void fisherBoardTest() {
    Boards.fischerChess().accept(board);

    Position whiteKing = board.getWhiteKingPosition();
    Position blackKing = board.getBlackKingPosition();
    List<Position> rooks = findAllPieceByTypeOneColumn(FigureType.ROOK);
    List<Position> queens = findAllPieceByTypeOneColumn(FigureType.QUEEN);
    List<Position> knights = findAllPieceByTypeOneColumn(FigureType.KNIGHT);
    List<Position> bishops = findAllPieceByTypeOneColumn(FigureType.BISHOP);

    boolean multicolorBishop = bishops.get(0).col().value() % 2 != bishops.get(2).col().value() % 2;
    boolean symmetricalRooks =
        rooks.get(0).col().value() == rooks.get(1).col().value()
            && rooks.get(2).col().value() == rooks.get(3).col().value();
    boolean symmetricalBishops =
        bishops.get(0).col().value() == bishops.get(1).col().value()
            && bishops.get(2).col().value() == bishops.get(3).col().value();
    boolean symmetricalKnights =
        knights.get(0).col().value() == knights.get(1).col().value()
            && knights.get(2).col().value() == knights.get(3).col().value();
    boolean symmetricalQueens = queens.get(0).col().value() == queens.get(1).col().value();

    Assertions.assertAll(
        () -> assertEquals(0, whiteKing.row().value(), "White king row"),
        () -> assertEquals(7, blackKing.row().value(), "Black king row"),
        () ->
            assertTrue(
                whiteKing.col().value() > 0 && whiteKing.col().value() < 7, "White king col"),
        () ->
            assertTrue(
                blackKing.col().value() > 0 && blackKing.col().value() < 7, "Black king col"),
        () -> assertEquals(4, rooks.size(), "Rooks"),
        () -> assertTrue(symmetricalRooks, "Symmetrical rooks"),
        () -> assertTrue(rooks.get(0).col().value() < whiteKing.col().value(), "Left white rook"),
        () -> assertTrue(rooks.get(1).col().value() < blackKing.col().value(), "Left black rook"),
        () -> assertTrue(rooks.get(2).col().value() > whiteKing.col().value(), "Right white rook"),
        () -> assertTrue(rooks.get(3).col().value() > blackKing.col().value(), "Right black rook"),
        () -> assertEquals(4, bishops.size(), "Bishops"),
        () -> assertTrue(symmetricalBishops, "Symmetrical bishops"),
        () -> assertTrue(multicolorBishop, "Multicolored bishops"),
        () -> assertEquals(2, queens.size(), "Queens"),
        () -> assertTrue(symmetricalQueens, "Symmetrical queens"),
        () -> assertEquals(4, knights.size(), "Knights"),
        () -> assertTrue(symmetricalKnights, "Symmetrical knights"));
  }

  private List<Position> findAllPieceByTypeOneColumn(FigureType figureType) {
    List<Position> pieces = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      if (board.getPiece(i, 0).getFigureType() == figureType
          && board.getPiece(i, 7).getFigureType() == figureType) {
        Position piece = new Position(new Column(i), new Row(0));
        pieces.add(piece);
        pieces.add(piece);
      }
    }

    return pieces;
  }
}
