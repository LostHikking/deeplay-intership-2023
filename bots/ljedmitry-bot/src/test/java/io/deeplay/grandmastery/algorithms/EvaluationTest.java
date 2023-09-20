package io.deeplay.grandmastery.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import io.deeplay.grandmastery.utils.Boards;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EvaluationTest {
  private Board board;

  @BeforeEach
  public void initBoard() {
    board = new HashBoard();
  }

  @Test
  void calculatePiecePriceTest() {
    Position queen = Position.fromString("a1");
    Position rook = Position.fromString("a2");
    Position bishop = Position.fromString("a3");
    Position knight = Position.fromString("a4");
    Position king = Position.fromString("a5");

    board.setPiece(queen, new Queen(Color.WHITE));
    board.setPiece(rook, new Rook(Color.WHITE));
    board.setPiece(king, new King(Color.WHITE));
    board.setPiece(bishop, new Bishop(Color.WHITE));
    board.setPiece(knight, new Knight(Color.WHITE));

    Assertions.assertAll(
        () -> assertEquals(9.0, Evaluation.calculatePiecePrice(board, queen, Color.WHITE)),
        () -> assertEquals(5.0, Evaluation.calculatePiecePrice(board, rook, Color.WHITE)),
        () -> assertEquals(3.0, Evaluation.calculatePiecePrice(board, bishop, Color.WHITE)),
        () -> assertEquals(3.0, Evaluation.calculatePiecePrice(board, knight, Color.WHITE)),
        () -> assertEquals(100.0, Evaluation.calculatePiecePrice(board, king, Color.WHITE)));
  }

  @ParameterizedTest
  @MethodSource("argsPawnPriceFactory")
  void calculateWhitePawnPriceTest(Position position, double expectEval) {
    board.setPiece(position, new Pawn(Color.WHITE));
    assertEquals(
        expectEval,
        Evaluation.calculatePiecePrice(board, position, Color.WHITE),
        0.000001,
        "Pos: " + Position.getString(position));
  }

  @ParameterizedTest
  @MethodSource("argsPawnPriceFactory")
  void calculateBlackPawnPriceTest(Position position, double expectEval) {
    board.setPiece(position, new Pawn(Color.BLACK));
    expectEval = Math.abs(expectEval % 1.0 - 0.5) + 1.0;

    assertEquals(
        expectEval,
        Evaluation.calculatePiecePrice(board, position, Color.BLACK),
        0.000001,
        "Pos: " + Position.getString(position));
  }

  private static Stream<Arguments> argsPawnPriceFactory() {
    return Stream.of(
        Arguments.of(Position.fromString("e2"), 1.0),
        Arguments.of(Position.fromString("e3"), 1.1),
        Arguments.of(Position.fromString("e6"), 1.4),
        Arguments.of(Position.fromString("e7"), 1.5),
        Arguments.of(Position.fromString("d2"), 1.0),
        Arguments.of(Position.fromString("d3"), 1.1),
        Arguments.of(Position.fromString("d6"), 1.4),
        Arguments.of(Position.fromString("d7"), 1.5),
        Arguments.of(Position.fromString("c3"), 1.1),
        Arguments.of(Position.fromString("c4"), 1.2),
        Arguments.of(Position.fromString("c5"), 1.3),
        Arguments.of(Position.fromString("c6"), 1.4),
        Arguments.of(Position.fromString("c7"), 1.5),
        Arguments.of(Position.fromString("f3"), 1.1),
        Arguments.of(Position.fromString("f4"), 1.2),
        Arguments.of(Position.fromString("f5"), 1.3),
        Arguments.of(Position.fromString("f6"), 1.4),
        Arguments.of(Position.fromString("f7"), 1.5));
  }

  @ParameterizedTest
  @MethodSource("argsCenterPawnPriceFactory")
  void calculateCenterPawnPriceTest(Position position, double expectEval, Color color) {
    board.setPiece(position, new Pawn(color));
    assertEquals(
        expectEval,
        Evaluation.calculatePiecePrice(board, position, color),
        0.000001,
        "Pos: " + Position.getString(position));
  }

  private static Stream<Arguments> argsCenterPawnPriceFactory() {
    return Stream.of(
        Arguments.of(Position.fromString("e4"), 1.5, Color.WHITE),
        Arguments.of(Position.fromString("e5"), 1.6, Color.WHITE),
        Arguments.of(Position.fromString("d4"), 1.3, Color.WHITE),
        Arguments.of(Position.fromString("d5"), 1.4, Color.WHITE),
        Arguments.of(Position.fromString("e4"), 1.6, Color.BLACK),
        Arguments.of(Position.fromString("e5"), 1.5, Color.BLACK),
        Arguments.of(Position.fromString("d4"), 1.4, Color.BLACK),
        Arguments.of(Position.fromString("d5"), 1.3, Color.BLACK));
  }

  @Test
  void calculateDoublePawnUpPriceTest() {
    board.setPiece(Position.fromString("a3"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("a4"), new Pawn(Color.WHITE));

    assertEquals(
        0.6,
        Evaluation.calculatePiecePrice(board, Position.fromString("a3"), Color.WHITE),
        0.000001);
  }

  @Test
  void calculateDoublePawnDownPriceTest() {
    board.setPiece(Position.fromString("a3"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("a4"), new Pawn(Color.WHITE));

    assertEquals(
        0.7,
        Evaluation.calculatePiecePrice(board, Position.fromString("a4"), Color.WHITE),
        0.000001);
  }

  @Test
  void calculateDoublePawnUpAndDownPriceTest() {
    board.setPiece(Position.fromString("a3"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("a4"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("a5"), new Pawn(Color.WHITE));

    assertEquals(
        0.2,
        Evaluation.calculatePiecePrice(board, Position.fromString("a4"), Color.WHITE),
        0.000001);
  }

  @Test
  void calculateNoDoublePawnPriceTest() {
    board.setPiece(Position.fromString("a3"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("b4"), new Pawn(Color.WHITE));

    assertEquals(
        1.2,
        Evaluation.calculatePiecePrice(board, Position.fromString("b4"), Color.WHITE),
        0.000001);
  }

  @Test
  void calculateWhitePiecesPriceTest() {
    Boards.defaultChess().accept(board);
    double expectEval = 139.0;

    Assertions.assertAll(
        () -> assertEquals(expectEval, Evaluation.calculatePiecesPrice(board, Color.WHITE)),
        () -> assertEquals(expectEval, Evaluation.calculatePiecesPrice(board, Color.BLACK)));
  }

  @ParameterizedTest
  @MethodSource("argsKingEndGameFactory")
  void kingEndGameTest(Position kingPos, double expectEval) {
    board.setPiece(kingPos, new King(Color.BLACK));
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("f1"), new Knight(Color.WHITE));

    assertEquals(
        expectEval,
        Evaluation.kingEndgameEval(board, Color.BLACK),
        0.000001,
        "Pos: " + Position.getString(kingPos));
  }

  private static Stream<Arguments> argsKingEndGameFactory() {
    return Stream.of(
        Arguments.of(Position.fromString("e4"), 1.0),
        Arguments.of(Position.fromString("e5"), 1.2),
        Arguments.of(Position.fromString("d4"), 1.1),
        Arguments.of(Position.fromString("d5"), 1.3),
        Arguments.of(Position.fromString("a8"), -4.6),
        Arguments.of(Position.fromString("a1"), -5.3),
        Arguments.of(Position.fromString("h8"), -4.6),
        Arguments.of(Position.fromString("h1"), -5.5));
  }

  @Test
  void kingNoEndGameTest() {
    board.setPiece(Position.fromString("e8"), new King(Color.BLACK));
    board.setPiece(Position.fromString("d8"), new Queen(Color.BLACK));
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("f1"), new Knight(Color.WHITE));

    assertEquals(0.0, Evaluation.kingEndgameEval(board, Color.BLACK));
  }

  @Test
  void securityPieceTest() {
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e2"), new Pawn(Color.WHITE));

    assertTrue(Evaluation.isSecurity(board, Position.fromString("e2"), Color.WHITE));
  }

  @Test
  void securityPawnPieceTest() {
    board.setPiece(Position.fromString("c8"), new Bishop(Color.BLACK));
    board.setPiece(Position.fromString("h3"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("g2"), new Pawn(Color.WHITE));

    assertTrue(Evaluation.isSecurity(board, Position.fromString("h3"), Color.WHITE));
  }

  @Test
  void noSecurityPieceTest() {
    board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("a2"), new Pawn(Color.WHITE));

    assertFalse(Evaluation.isSecurity(board, Position.fromString("a2"), Color.WHITE));
  }

  @Test
  void zeroExchangePieceTest() {
    board.setPiece(Position.fromString("a1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("e2"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("e8"), new Queen(Color.BLACK));

    assertEquals(0, Evaluation.pieceExchange(board, Color.WHITE));
  }

  @Test
  void securityPiecePawnPromotionTest() {
    board.setPiece(Position.fromString("e8"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("f7"), new Pawn(Color.WHITE));

    assertTrue(Evaluation.isSecurity(board, Position.fromString("e8"), Color.WHITE));
  }

  @Test
  void lossPieceTest() {
    board.setPiece(Position.fromString("a1"), new King(Color.WHITE));
    board.setPiece(Position.fromString("e2"), new Queen(Color.WHITE));
    board.setPiece(Position.fromString("e8"), new Queen(Color.BLACK));

    assertEquals(-9.0, Evaluation.pieceExchange(board, Color.WHITE));
  }

  @Test
  void lossPiecePawnPromotionTest() {
    board.setPiece(Position.fromString("f7"), new Pawn(Color.WHITE));
    board.setPiece(Position.fromString("e8"), new Queen(Color.BLACK));

    assertEquals(-9.0, Evaluation.pieceExchange(board, Color.BLACK));
  }

  @Test
  void evaluationOurBoardTest() {
    Board firstBoard = new HashBoard();
    firstBoard.setPiece(Position.fromString("a2"), new Rook(Color.WHITE));
    firstBoard.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    firstBoard.setPiece(Position.fromString("h8"), new King(Color.BLACK));
    firstBoard.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    firstBoard.setPiece(Position.fromString("h2"), new King(Color.WHITE));
    firstBoard.setLastMove(LongAlgebraicNotation.getMoveFromString("c3c1"));

    GameHistory firstGameHistory = new GameHistory();
    firstGameHistory.startup(firstBoard);

    Board secondBoard = new HashBoard();
    secondBoard.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    secondBoard.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    secondBoard.setPiece(Position.fromString("h8"), new King(Color.BLACK));
    secondBoard.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    secondBoard.setPiece(Position.fromString("h2"), new King(Color.WHITE));
    secondBoard.setLastMove(LongAlgebraicNotation.getMoveFromString("c3c1"));

    GameHistory secondGameHistory = new GameHistory();
    secondGameHistory.startup(secondBoard);

    double firstEval = Evaluation.evaluationFunc(firstBoard, secondGameHistory, Color.WHITE, false);
    double secondEval =
        Evaluation.evaluationFunc(secondBoard, secondGameHistory, Color.WHITE, false);
    assertTrue(firstEval < secondEval);
  }

  @Test
  void evaluationEnemyBoardTest() {
    Board firstBoard = new HashBoard();
    firstBoard.setPiece(Position.fromString("a2"), new Rook(Color.WHITE));
    firstBoard.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    firstBoard.setPiece(Position.fromString("h8"), new King(Color.BLACK));
    firstBoard.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    firstBoard.setPiece(Position.fromString("h2"), new King(Color.WHITE));
    firstBoard.setLastMove(LongAlgebraicNotation.getMoveFromString("c3c1"));

    GameHistory firstGameHistory = new GameHistory();
    firstGameHistory.startup(firstBoard);

    Board secondBoard = new HashBoard();
    secondBoard.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    secondBoard.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    secondBoard.setPiece(Position.fromString("h8"), new King(Color.BLACK));
    secondBoard.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    secondBoard.setPiece(Position.fromString("h2"), new King(Color.WHITE));
    secondBoard.setLastMove(LongAlgebraicNotation.getMoveFromString("c3c1"));

    GameHistory secondGameHistory = new GameHistory();
    secondGameHistory.startup(secondBoard);

    double firstEval = Evaluation.evaluationFunc(firstBoard, secondGameHistory, Color.WHITE, true);
    double secondEval =
        Evaluation.evaluationFunc(secondBoard, secondGameHistory, Color.WHITE, true);
    assertEquals(firstEval, secondEval);
  }

  @Test
  void evaluationBoardOpponentMateTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("b1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("a8"), new King(Color.BLACK));
    board.setPiece(Position.fromString("c1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("h1"), new King(Color.WHITE));

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertEquals(1, Evaluation.evaluationFunc(board, gameHistory, Color.WHITE, true));
  }

  @Test
  void evaluationBoardMateUsTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("a1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("b1"), new Rook(Color.BLACK));
    board.setPiece(Position.fromString("a8"), new King(Color.WHITE));
    board.setPiece(Position.fromString("c1"), new Rook(Color.WHITE));
    board.setPiece(Position.fromString("h1"), new King(Color.BLACK));

    GameHistory gameHistory = new GameHistory();
    gameHistory.startup(board);

    assertEquals(-1, Evaluation.evaluationFunc(board, gameHistory, Color.WHITE, true));
  }

  @Test
  void evaluationBoardDrawTest() {
    Board board = new HashBoard();
    board.setPiece(Position.fromString("e4"), new King(Color.WHITE));
    board.setPiece(Position.fromString("g7"), new King(Color.BLACK));

    Move move = LongAlgebraicNotation.getMoveFromString("e3e4");

    GameHistory gameHistory = new GameHistory();
    gameHistory.addBoard(board);
    gameHistory.makeMove(move);
    board.setLastMove(move);

    assertEquals(0, Evaluation.evaluationFunc(board, gameHistory, Color.WHITE, true));
  }
}
