package io.deeplay.grandmastery.figures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class PawnTest {
  private Board board;

  /** Создание новой доски. */
  @BeforeEach
  public void init() {
    board = new HashBoard();
  }

  /**
   * Тест для проверки стандартных ситуаций для хода пешки.
   *
   * @param moveStr ход в длинной алгебраической нотации
   * @param canMove ожидаемый результат - возможность перемещения пешки
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @MethodSource("defaultMove")
  public void pawnDefaultMoveTest(String moveStr, boolean canMove, Color color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(color);
    board.setPiece(move.from(), pawn);
    pawn.move(board, move);

    if (canMove) {
      assertSame(pawn, board.getPiece(move.to()), "Check " + color + " pawn move " + moveStr);
    } else {
      assertFalse(board.hasPiece(move.to()), "Check " + color + " pawn not move " + moveStr);
    }
  }

  private static Stream<Arguments> defaultMove() {
    return Stream.of(
        Arguments.of("e2e3", true, Color.WHITE),
        Arguments.of("e2e4", true, Color.WHITE),
        Arguments.of("e2e5", false, Color.WHITE),
        Arguments.of("e2f3", false, Color.WHITE),
        Arguments.of("e2d3", false, Color.WHITE),
        Arguments.of("e2e1", false, Color.WHITE),
        Arguments.of("e2f1", false, Color.WHITE),
        Arguments.of("e2d1", false, Color.WHITE),
        Arguments.of("e2f2", false, Color.WHITE),
        Arguments.of("e2d2", false, Color.WHITE),
        Arguments.of("e7e6", true, Color.BLACK),
        Arguments.of("e7e5", true, Color.BLACK),
        Arguments.of("e7e4", false, Color.BLACK),
        Arguments.of("e7f6", false, Color.BLACK),
        Arguments.of("e7d6", false, Color.BLACK),
        Arguments.of("e7e8", false, Color.BLACK),
        Arguments.of("e7f8", false, Color.BLACK),
        Arguments.of("e7d8", false, Color.BLACK),
        Arguments.of("e7f7", false, Color.BLACK),
        Arguments.of("e7d7", false, Color.BLACK));
  }

  /**
   * Тест для проверки ограничения движения на две клетки вперед после первого хода.
   *
   * @param secondMoveStr второй ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e4e6, WHITE", "e5e3, BLACK"})
  public void noMoveForwardTwoSquareAfterMovingTest(String secondMoveStr, String color) {
    Move firstMove;
    if ("WHITE".equals(color)) {
      firstMove = LongAlgebraicNotation.getMoveFromString("e2e4");
    } else {
      firstMove = LongAlgebraicNotation.getMoveFromString("e7e5");
    }

    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(firstMove.from(), pawn);
    pawn.move(board, firstMove);

    Move secondMove = LongAlgebraicNotation.getMoveFromString(secondMoveStr);
    assertFalse(pawn.move(board, secondMove), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки блокировки движения пешки на две клетки вперед, если путь заблокирован другой
   * фигурой.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e2e4, WHITE", "e7e5, BLACK"})
  public void noMoveForwardTwoSquareBlockTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(move.from(), pawn);
    if ("WHITE".equals(color)) {
      board.setPiece(Position.getPositionFromString("e3"), new Pawn(Color.valueOf(color)));
    } else {
      board.setPiece(Position.getPositionFromString("e6"), new Pawn(Color.valueOf(color)));
    }

    assertFalse(pawn.move(board, move), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки возможности захвата другой фигуры, противоположного цвета, пешкой. Фигура для
   * захвата находится впереди пешки.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3f4, WHITE", "e3d4, WHITE", "e5d4, BLACK", "e5f4, BLACK"})
  public void captureAnotherPieceTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(move.from(), pawn);

    if ("WHITE".equals(color)) {
      board.setPiece(move.to(), new Pawn(Color.BLACK));
    } else {
      board.setPiece(move.to(), new Pawn(Color.WHITE));
    }

    Assertions.assertAll(
        "Check " + color + " pawn",
        () -> assertTrue(pawn.move(board, move)),
        () -> assertSame(pawn, board.getPiece(move.to()), "Pawn in place of another piece"));
  }

  /**
   * Тест для проверки невозможности захвата другой фигуры, противоположного цвета, пешкой. Фигура
   * для захвата находится позади пешки.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3f2, WHITE", "e3d2, WHITE", "e5d6, BLACK", "e5f6, BLACK"})
  public void noCanCaptureAnotherPieceTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(move.from(), pawn);

    if ("WHITE".equals(color)) {
      board.setPiece(move.to(), new Pawn(Color.BLACK));
    } else {
      board.setPiece(move.to(), new Pawn(Color.WHITE));
    }

    Assertions.assertAll(
        "Check " + color + " pawn",
        () -> assertFalse(pawn.move(board, move)),
        () -> assertNotSame(pawn, board.getPiece(move.to()), "Board has not changed"));
  }

  /**
   * Тест для проверки невозможности захвата другой фигуры, своего цвета, пешкой.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3f4, WHITE", "e5f4, BLACK"})
  public void noCapturePieceSameColorTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(move.from(), pawn);
    board.setPiece(move.to(), new Pawn(Color.valueOf(color)));

    assertFalse(pawn.move(board, move), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки невозможности захвата короля пешкой.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e3f4, WHITE", "e5f4, BLACK"})
  public void noCaptureKingTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(move.from(), pawn);

    if ("WHITE".equals(color)) {
      board.setPiece(move.to(), new King(Color.BLACK));
    } else {
      board.setPiece(move.to(), new King(Color.WHITE));
    }

    assertFalse(pawn.move(board, move), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки блокировки движения пешки вперед, если путь заблокирован другой фигурой.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(value = {"e2e3, WHITE", "e7e6, BLACK"})
  public void blockForwardMoveTest(String moveStr, String color) {
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    Piece pawn = new Pawn(Color.valueOf(color));
    board.setPiece(move.from(), pawn);
    board.setPiece(move.to(), new Pawn(Color.valueOf(color)));

    assertFalse(pawn.move(board, move), "Check " + color + " pawn");
  }

  /**
   * Тест для проверки захвата по правилам "взятия на проходе". Проверяет, что белая пешка может
   * захватить черную пешку на проходе.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   */
  @ParameterizedTest
  @ValueSource(strings = {"d5c6", "b5c6"})
  public void captureEnPassantWhitePawnTest(String moveStr) {
    Move lastMove = LongAlgebraicNotation.getMoveFromString("c7c5");
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);

    Piece whitePawn = new Pawn(Color.WHITE);
    Piece blackPawn = new Pawn(Color.BLACK);
    board.setPiece(move.from(), whitePawn);
    board.setPiece(lastMove.to(), blackPawn);
    board.setLastMove(lastMove);

    Assertions.assertAll(
        () -> assertTrue(whitePawn.move(board, move)),
        () -> assertSame(whitePawn, board.getPiece(move.to()), "Check white pawn position"),
        () -> assertFalse(board.hasPiece(lastMove.to()), "Check black pawn remove"));
  }

  /**
   * Тест для проверки захвата по правилам "взятия на проходе". Проверяет, что черная пешка может
   * захватить белую пешку на проходе.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   */
  @ParameterizedTest
  @ValueSource(strings = {"f4e3", "d4e3"})
  public void captureEnPassantBlackPawnTest(String moveStr) {
    Move lastMove = LongAlgebraicNotation.getMoveFromString("e2e4");
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);

    Piece whitePawn = new Pawn(Color.WHITE);
    Piece blackPawn = new Pawn(Color.BLACK);
    board.setPiece(move.from(), blackPawn);
    board.setPiece(lastMove.to(), whitePawn);
    board.setLastMove(lastMove);

    Assertions.assertAll(
        () -> assertTrue(blackPawn.move(board, move)),
        () -> assertSame(blackPawn, board.getPiece(move.to()), "Check black pawn position"),
        () -> assertFalse(board.hasPiece(lastMove.to()), "Check white pawn remove"));
  }

  /**
   * Тест для проверки того, что "взятие на проходе" не работает другая пешка переместилась на одну
   * клетку.
   */
  @Test
  public void notCaptureEnPassantTest() {
    Move lastMove = LongAlgebraicNotation.getMoveFromString("c6c5");
    Move move = LongAlgebraicNotation.getMoveFromString("d5c6");

    Piece whitePawn = new Pawn(Color.WHITE);
    Piece blackPawn = new Pawn(Color.BLACK);
    board.setPiece(move.from(), whitePawn);
    board.setPiece(lastMove.to(), blackPawn);
    board.setLastMove(lastMove);

    Assertions.assertAll(
        () -> assertFalse(whitePawn.move(board, move)),
        () -> assertSame(whitePawn, board.getPiece(move.from()), "Check white pawn position"),
        () ->
            assertSame(
                blackPawn, board.getPiece(lastMove.to()), "Check black blackPawn not remove"));
  }

  /**
   * Тест для проверки того, что "взятие на проходе" не работает с другими фигурами, кроме пешек.
   */
  @Test
  @DisplayName("Capture En Passant not work piece other than pawn")
  public void notCaptureEnPassantAnotherPieceTest() {
    Move lastMove = LongAlgebraicNotation.getMoveFromString("c7c5");
    Move move = LongAlgebraicNotation.getMoveFromString("d5c6");

    Piece whitePawn = new Pawn(Color.WHITE);
    Piece blackQueen = new Queen(Color.BLACK);
    board.setPiece(move.from(), whitePawn);
    board.setPiece(lastMove.to(), blackQueen);
    board.setLastMove(lastMove);

    Assertions.assertAll(
        () -> assertFalse(whitePawn.move(board, move)),
        () -> assertSame(whitePawn, board.getPiece(move.from()), "Check white pawn position"),
        () ->
            assertSame(
                blackQueen, board.getPiece(lastMove.to()), "Check black blackQueen not remove"));
  }

  /** Тест для проверки того, что "взятие на проходе" не работает с пешкой того же цвета. */
  @Test
  public void noCaptureEnPassantOneColorPawnsTest() {
    Move lastMove = LongAlgebraicNotation.getMoveFromString("e2e4");
    Move move = LongAlgebraicNotation.getMoveFromString("d2e3");

    Piece whitePawn = new Pawn(Color.WHITE);
    Piece whitePawn2 = new Pawn(Color.BLACK);
    board.setPiece(move.from(), whitePawn);
    board.setPiece(lastMove.to(), whitePawn2);
    board.setLastMove(lastMove);

    Assertions.assertAll(
        () -> assertFalse(whitePawn.move(board, move)),
        () -> assertSame(whitePawn, board.getPiece(move.from()), "Check white pawn position"),
        () ->
            assertSame(whitePawn2, board.getPiece(lastMove.to()), "Check white pawn 2 not remove"));
  }

  /**
   * Тест для проверки превращения пешки, в фигуру заданного типа при достижении последнего ряда
   * доски.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(
      value = {
        "e7e8r, WHITE",
        "e7e8n, WHITE",
        "e7e8q, WHITE",
        "e7e8b, WHITE",
        "e2e1r, BLACK",
        "e2e1n, BLACK",
        "e2e1q, BLACK",
        "e2e1b, BLACK"
      })
  public void reviveTest(String moveStr, String color) {
    Piece pawn = new Pawn(Color.valueOf(color));
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    board.setPiece(move.from(), pawn);

    Assertions.assertAll(
        () -> assertTrue(pawn.move(board, move)),
        () -> assertNull(board.getPiece(move.from()), "Check pawn remove"),
        () ->
            assertEquals(
                move.promotionPiece(),
                board.getPiece(move.to()).getFigureType(),
                "Check pawn revive"));
  }

  /**
   * Тест для проверки невозможности превращения пешки в короля или другую пешку.
   *
   * @param moveStr ход пешки в длинной алгебраической нотации
   * @param color цвет пешки (WHITE - белая, BLACK - черная)
   */
  @ParameterizedTest
  @CsvSource(
      value = {
        "e7e8k, WHITE",
        "e7e8p, WHITE",
        "e2e1k, BLACK",
        "e2e1p, BLACK",
      })
  public void notReviveTest(String moveStr, String color) {
    Piece pawn = new Pawn(Color.valueOf(color));
    Move move = LongAlgebraicNotation.getMoveFromString(moveStr);
    board.setPiece(move.from(), pawn);

    Assertions.assertAll(
        () -> assertFalse(pawn.move(board, move)),
        () -> assertSame(pawn, board.getPiece(move.from()), "Pawn stay old place"),
        () -> assertNotSame(pawn, board.getPiece(move.to()), "Pawn not move"));
  }

  /** Тест для проверки превращения пешки, при взятии другой фигуры и достижении последнего ряда. */
  @Test
  public void reviveWhenCaptureTest() {
    Piece pawn = new Pawn(Color.WHITE);
    Move move = LongAlgebraicNotation.getMoveFromString("e7f8q");
    board.setPiece(move.from(), pawn);
    board.setPiece(move.to(), new Rook(Color.BLACK));

    Assertions.assertAll(
        () -> assertTrue(pawn.move(board, move)),
        () -> assertNull(board.getPiece(move.from()), "Check pawn remove"),
        () ->
            assertEquals(
                move.promotionPiece(),
                board.getPiece(move.to()).getFigureType(),
                "Check pawn revive"));
  }

  @Test
  public void allMovesWhitePawnTest() {
    Piece pawn = new Pawn(Color.WHITE);
    Position position = Position.getPositionFromString("e2");

    board.setPiece(position, pawn);
    board.setPiece(Position.getPositionFromString("d2"), new Pawn(Color.BLACK));
    board.setPiece(Position.getPositionFromString("f3"), new Pawn(Color.BLACK));
    board.setPiece(Position.getPositionFromString("f1"), new Pawn(Color.BLACK));
    board.setPiece(Position.getPositionFromString("d1"), new Pawn(Color.BLACK));
    board.setLastMove(LongAlgebraicNotation.getMoveFromString("d4d2"));

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2e3"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2e4"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2f3"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2d3"));

    assertEquals(expectMoves, pawn.getAllMoves(board, position));
  }

  @Test
  public void allMovesBlackPawnTest() {
    Piece pawn = new Pawn(Color.BLACK);
    Position position = Position.getPositionFromString("e7");

    board.setPiece(position, pawn);
    board.setPiece(Position.getPositionFromString("d6"), new Pawn(Color.WHITE));
    board.setPiece(Position.getPositionFromString("f7"), new Pawn(Color.WHITE));
    board.setPiece(Position.getPositionFromString("f8"), new Pawn(Color.WHITE));
    board.setPiece(Position.getPositionFromString("d8"), new Pawn(Color.WHITE));
    board.setLastMove(LongAlgebraicNotation.getMoveFromString("f5f7"));

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7e6"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7e5"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7f6"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7d6"));

    assertEquals(expectMoves, pawn.getAllMoves(board, position));
  }

  @Test
  public void allMovesReviveWhitePawnTest() {
    Piece pawn = new Pawn(Color.WHITE);
    Position position = Position.getPositionFromString("e7");

    board.setPiece(position, pawn);
    board.setPiece(Position.getPositionFromString("f8"), new Knight(Color.BLACK));
    board.setPiece(Position.getPositionFromString("d8"), new Knight(Color.BLACK));

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7e8b"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7e8r"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7e8q"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7e8n"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7f8b"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7f8r"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7f8q"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7f8n"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7d8b"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7d8r"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7d8q"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e7d8n"));

    assertEquals(expectMoves, pawn.getAllMoves(board, position));
  }

  @Test
  public void allMovesReviveBlackPawnTest() {
    Piece pawn = new Pawn(Color.BLACK);
    Position position = Position.getPositionFromString("e2");

    board.setPiece(position, pawn);
    board.setPiece(Position.getPositionFromString("f1"), new Knight(Color.WHITE));
    board.setPiece(Position.getPositionFromString("d1"), new Knight(Color.WHITE));

    List<Move> expectMoves = new ArrayList<>();
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2e1b"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2e1r"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2e1q"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2e1n"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2f1b"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2f1r"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2f1q"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2f1n"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2d1b"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2d1r"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2d1q"));
    expectMoves.add(LongAlgebraicNotation.getMoveFromString("e2d1n"));

    assertEquals(expectMoves, pawn.getAllMoves(board, position));
  }
}
