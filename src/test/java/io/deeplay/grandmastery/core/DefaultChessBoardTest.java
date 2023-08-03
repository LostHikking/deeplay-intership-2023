package io.deeplay.grandmastery.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.BoardUtils;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class DefaultChessBoardTest {
  private static Board board;

  @BeforeAll
  public static void init() {
    board = new HashBoard();
    BoardUtils.defaultChess().accept(board);
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
    for (int i = 0; i < 2; i++) {
      Piece piece;
      Color actualColor;
      if (i == 0) {
        piece = board.getPiece(col, 0);
        actualColor = Color.WHITE;
      } else {
        piece = board.getPiece(col, 7);
        actualColor = Color.BLACK;
      }

      Assertions.assertAll(
          "Check " + actualColor + " " + figureType,
          () -> assertEquals(figureType, piece.getFigureType()),
          () -> assertEquals(actualColor, piece.getColor()));
    }
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
}
