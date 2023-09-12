package io.deeplay.grandmastery.figures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import jdk.jfr.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class KingTest {
  private Board board;

  @BeforeEach
  public void init() {
    board = new HashBoard();
  }

  /**
   * Тест для проверки стандартных ситуаций для хода короля.
   *
   * @param moveStr строка с ходом в длинной алгебраической нотации
   * @param canMove ожидаемый результат - возможность перемещения короля
   */
  @ParameterizedTest
  @MethodSource("defaultMove")
  public void kingDefaultMoveTest(String moveStr, boolean canMove) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece king = new King(Color.WHITE);
    board.setPiece(move.from(), king);
    king.move(board, move);

    if (canMove) {
      assertSame(king, board.getPiece(move.to()), "Check king move " + moveStr);
    } else {
      assertFalse(board.hasPiece(move.to()), "Check king not move " + moveStr);
    }
  }

  private static Stream<Arguments> defaultMove() {
    return Stream.of(
        Arguments.of("e2e3", true),
        Arguments.of("e2e4", false),
        Arguments.of("e2f3", true),
        Arguments.of("e2g4", false),
        Arguments.of("e2d3", true),
        Arguments.of("e2c4", false),
        Arguments.of("e2e1", true),
        Arguments.of("e3e1", false),
        Arguments.of("e2f1", true),
        Arguments.of("e3g1", false),
        Arguments.of("e2d1", true),
        Arguments.of("e3c1", false),
        Arguments.of("e2f2", true),
        Arguments.of("e2g2", false),
        Arguments.of("e2d2", true),
        Arguments.of("e2c2", false));
  }

  /**
   * Тест для проверки возможности захвата другой фигуры, противоположного цвета, королем.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет короля (WHITE - белый, BLACK - черный)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3e4, WHITE", "e5e4, BLACK"})
  public void captureAnotherPieceTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece king = new King(Color.valueOf(color));
    board.setPiece(move.from(), king);

    if ("WHITE".equals(color)) {
      board.setPiece(move.to(), new Pawn(Color.BLACK));
    } else {
      board.setPiece(move.to(), new Pawn(Color.WHITE));
    }

    Assertions.assertAll(
        "Check " + color + " pawn",
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "Pawn in place of another piece"));
  }

  /**
   * Тест для проверки невозможности захвата другой фигуры, своего цвета, королем.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет короля (WHITE - белый, BLACK - черный)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3e4, WHITE", "e5e4, BLACK"})
  public void noCapturePieceSameColorTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece king = new King(Color.valueOf(color));
    board.setPiece(move.from(), king);
    board.setPiece(move.to(), new Pawn(Color.valueOf(color)));

    assertFalse(king.move(board, move), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки невозможности захвата короля другим королем.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет короля (WHITE - белый, BLACK - черный)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3e4, WHITE", "e5e4, BLACK"})
  public void noCaptureKingTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece king = new King(Color.valueOf(color));
    board.setPiece(move.from(), king);

    if ("WHITE".equals(color)) {
      board.setPiece(move.to(), new King(Color.BLACK));
    } else {
      board.setPiece(move.to(), new King(Color.WHITE));
    }

    assertFalse(king.move(board, move), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки всех возможных рокировок влево белыми фигурами.
   *
   * @param kingPos Позиция короля перед рокировкой.
   * @param rookPos Позиция ладьи перед рокировкой.
   */
  @ParameterizedTest
  @MethodSource("positionsForLeftWhiteCastling")
  public void leftCastlingWhiteTest(Position kingPos, Position rookPos) {
    Move move = new Move(kingPos, Position.fromString("c1"), null);
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    board.setPiece(kingPos, king);
    board.setPiece(rookPos, rook);

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "King after castling"),
        () -> assertSame(rook, board.getPiece(Position.fromString("d1")), "Rook after castling"));
  }

  private static Stream<Arguments> positionsForLeftWhiteCastling() {
    List<Arguments> positions = new ArrayList<>();

    String[] columns = {"a", "b", "c", "d", "e", "f", "g"};
    for (int i = 1; i < columns.length; i++) {
      for (int j = 0; j < i; j++) {
        String rook = columns[j] + "1";
        String king = columns[i] + "1";
        positions.add(Arguments.of(Position.fromString(king), Position.fromString(rook)));
      }
    }

    return positions.stream();
  }

  /**
   * Тест для проверки всех возможных рокировок вправо белыми фигурами.
   *
   * @param kingPos Позиция короля перед рокировкой.
   * @param rookPos Позиция ладьи перед рокировкой.
   */
  @ParameterizedTest
  @MethodSource("positionsForRightWhiteCastling")
  public void rightCastlingWhiteTest(Position kingPos, Position rookPos) {
    Move move = new Move(kingPos, Position.fromString("g1"), null);
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    board.setPiece(kingPos, king);
    board.setPiece(rookPos, rook);

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "King after castling"),
        () -> assertSame(rook, board.getPiece(Position.fromString("f1")), "Rook after castling"));
  }

  private static Stream<Arguments> positionsForRightWhiteCastling() {
    List<Arguments> positions = new ArrayList<>();

    String[] columns = {"b", "c", "d", "e", "f", "g", "h"};
    for (int i = 0; i < columns.length; i++) {
      for (int j = i + 1; j < columns.length; j++) {
        String rook = columns[j] + "1";
        String king = columns[i] + "1";
        positions.add(Arguments.of(Position.fromString(king), Position.fromString(rook)));
      }
    }

    return positions.stream();
  }

  /**
   * Тест для проверки всех возможных рокировок влево черными фигурами.
   *
   * @param kingPos Позиция короля перед рокировкой.
   * @param rookPos Позиция ладьи перед рокировкой.
   */
  @ParameterizedTest
  @MethodSource("positionsForLeftBlackCastling")
  public void leftCastlingBlackTest(Position kingPos, Position rookPos) {
    Move move = new Move(kingPos, Position.fromString("c8"), null);
    Piece king = new King(Color.BLACK);
    Piece rook = new Rook(Color.BLACK);
    board.setPiece(kingPos, king);
    board.setPiece(rookPos, rook);

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "King after castling"),
        () -> assertSame(rook, board.getPiece(Position.fromString("d8")), "Rook after castling"));
  }

  private static Stream<Arguments> positionsForLeftBlackCastling() {
    List<Arguments> positions = new ArrayList<>();

    String[] columns = {"a", "b", "c", "d", "e", "f", "g"};
    for (int i = 1; i < columns.length; i++) {
      for (int j = 0; j < i; j++) {
        String rook = columns[j] + "8";
        String king = columns[i] + "8";
        positions.add(Arguments.of(Position.fromString(king), Position.fromString(rook)));
      }
    }

    return positions.stream();
  }

  /**
   * Тест для проверки всех возможных рокировок вправо черными фигурами.
   *
   * @param kingPos Позиция короля перед рокировкой.
   * @param rookPos Позиция ладьи перед рокировкой.
   */
  @ParameterizedTest
  @MethodSource("positionsForRightBlackCastling")
  public void rightCastlingBlackTest(Position kingPos, Position rookPos) {
    Move move = new Move(kingPos, Position.fromString("g8"), null);
    Piece king = new King(Color.BLACK);
    Piece rook = new Rook(Color.BLACK);
    board.setPiece(kingPos, king);
    board.setPiece(rookPos, rook);

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "King after castling"),
        () -> assertSame(rook, board.getPiece(Position.fromString("f8")), "Rook after castling"));
  }

  private static Stream<Arguments> positionsForRightBlackCastling() {
    List<Arguments> positions = new ArrayList<>();

    String[] columns = {"b", "c", "d", "e", "f", "g", "h"};
    for (int i = 0; i < columns.length; i++) {
      for (int j = i + 1; j < columns.length; j++) {
        String rook = columns[j] + "8";
        String king = columns[i] + "8";
        positions.add(Arguments.of(Position.fromString(king), Position.fromString(rook)));
      }
    }

    return positions.stream();
  }

  @Test
  public void notCastlingKingHasMovedTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1g1");
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    king.setMoved(true);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("h1"), rook);

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "King not changed"),
        () -> assertSame(rook, board.getPiece(Position.fromString("h1")), "Rook not changed"));
  }

  @Test
  public void notCastlingRookHasMovedTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1c1");
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    rook.setMoved(true);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("a1"), rook);

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "King not changed"),
        () -> assertSame(rook, board.getPiece(Position.fromString("a1")), "Rook not changed"));
  }

  @Test
  public void notCastlingByNoRookTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1c1");
    Piece king = new King(Color.WHITE);
    board.setPiece(move.from(), king);

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "King not changed"));
  }

  @Test
  public void castlingBlockAnotherPieceTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1c1");
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    Piece knight = new Knight(Color.WHITE);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("a1"), rook);
    board.setPiece(Position.fromString("b1"), knight);

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "King not changed"),
        () -> assertSame(rook, board.getPiece(Position.fromString("a1")), "Rook not changed"));
  }

  @Test
  public void castlingBlockEnemyPieceTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1c1");
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    Piece blackRook = new Rook(Color.BLACK);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("a1"), rook);
    board.setPiece(Position.fromString("d7"), blackRook);

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "King not changed"),
        () -> assertSame(rook, board.getPiece(Position.fromString("a1")), "Rook not changed"));
  }

  @Test
  public void castlingNoBlockEnemyPieceTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1c1");
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    Piece blackRook = new Rook(Color.BLACK);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("a1"), rook);
    board.setPiece(Position.fromString("b7"), blackRook);

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "King after castling"),
        () -> assertSame(rook, board.getPiece(Position.fromString("d1")), "Rook after castling"));
  }

  @Test
  public void skipCastlingTest() {
    King king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    board.setPiece(Position.fromString("e1"), king);
    board.setPiece(Position.fromString("a1"), rook);

    Move move = LongAlgebraicNotation.getMoveFromString("e1e2");
    king.getAllMoves(board, Position.fromString("e1"));

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "Check king pos"),
        () -> assertSame(rook, board.getPiece(Position.fromString("a1")), "Check rook pos"));
  }

  @Test
  public void friendPieceNoBlockCastlingTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1g1");
    Piece king = new King(Color.WHITE);
    Piece rook = new Rook(Color.WHITE);
    Piece whiteKnight = new Rook(Color.WHITE);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("h1"), rook);
    board.setPiece(Position.fromString("f3"), whiteKnight);

    Assertions.assertAll(
        () -> assertTrue(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.to()), "King after castling"),
        () -> assertSame(rook, board.getPiece(Position.fromString("f1")), "Rook after castling"));
  }

  @Test
  public void noCaptureProtectedPieceTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e8f7");
    Piece king = new King(Color.BLACK);
    Piece queen = new Queen(Color.WHITE);
    board.setPiece(move.from(), king);
    board.setPiece(move.to(), queen);
    board.setPiece(Position.fromString("c4"), new Bishop(Color.WHITE));

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "Check king position"),
        () -> assertSame(queen, board.getPiece(move.to()), "Check queen position"));
  }

  @Test
  public void noMoveKingIsCheckTest() {
    Move move = LongAlgebraicNotation.getMoveFromString("e1e2");
    Piece king = new King(Color.WHITE);
    Piece queen = new Queen(Color.BLACK);
    board.setPiece(move.from(), king);
    board.setPiece(Position.fromString("e8"), queen);

    Assertions.assertAll(
        () -> assertFalse(king.move(board, move)),
        () -> assertSame(king, board.getPiece(move.from()), "Check king position"));
  }

  @Test
  public void allMovesKingWithoutCastlingTest() {
    Piece king = new King(Color.WHITE);
    Position position = Position.fromString("e4");
    board.setPiece(position, king);

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4e5"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4f5"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4f4"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4f3"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4e3"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4d3"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4d4"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e4d5"));

    assertEquals(expectMoves, king.getAllMoves(board, position));
  }

  @Test
  @Description("Pawns block forward moves, but all castling possible")
  public void allMovesKingInStartPositionTest() {
    Piece king = new King(Color.WHITE);
    Position position = Position.fromString("e1");
    board.setPiece(position, king);
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("e2"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("d2"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("f2"), new Pawn(Color.WHITE));

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e1f1"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e1d1"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e1c1"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e1g1"));

    assertEquals(expectMoves, king.getAllMoves(board, position));
  }

  @Test
  public void getAllCastlingForFishersGameTest() {
    Piece king = new King(Color.WHITE);
    Position position = Position.fromString("c1");
    board.setPiece(position, king);
    board.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("e1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("b2"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("c2"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("d2"), new Pawn(Color.WHITE));

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("c1d1"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("c1c1"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("c1g1"));

    assertEquals(expectMoves, king.getAllMoves(board, position));
  }
}
