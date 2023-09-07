package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Algorithms {
  public static final double MAX_EVAL = 1.0;
  public static final double MIN_EVAL = -1.0;

  private static final Map<Integer, Integer> PAWN_PRICE =
      Map.of(1, 100, 2, 100, 3, 110, 4, 150, 5, 200, 6, 300);
  private static final Map<FigureType, Integer> PIECE_PRICE =
      Map.of(
          FigureType.KNIGHT,
          288,
          FigureType.BISHOP,
          345,
          FigureType.ROOK,
          480,
          FigureType.QUEEN,
          1077,
          FigureType.KING,
          0);

  public static GameHistory copyHistoryAndMove(Board board, GameHistory gameHistory) {
    GameHistory copyHistory = gameHistory.getCopy();
    copyHistory.addBoard(board);
    copyHistory.makeMove(board.getLastMove());

    return copyHistory;
  }

  public static Board copyAndMove(Move move, Board board) {
    Board copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);

    Piece piece = copyBoard.getPiece(move.from());
    piece.move(copyBoard, move);
    copyBoard.setLastMove(move);

    return copyBoard;
  }

  public static List<Move> getPossibleMoves(Board board, Color color) {
    List<Move> moves = new ArrayList<>();
    List<Position> positions = board.getAllPiecePositionByColor(color);

    for (Position position : positions) {
      moves.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return moves;
  }

  public static boolean isGameOver(Board board, GameHistory gameHistory) {
    return GameStateChecker.isMate(board, Color.WHITE)
        || GameStateChecker.isMate(board, Color.BLACK)
        || GameStateChecker.isDraw(board, gameHistory);
  }

  public static double evaluationFunc(Board board, GameHistory gameHistory, Color botColor) {
    if (GameStateChecker.isMate(board, inversColor(botColor))) {
      return MAX_EVAL;
    } else if (GameStateChecker.isMate(board, botColor)) {
      return MIN_EVAL;
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return 0;
    }

    int our_rate = evaluationBoard(board, botColor);
    int opponent_rate = evaluationBoard(board, inversColor(botColor));

    int result = our_rate - opponent_rate;
    return result / Math.pow(10, countDigit(Math.abs(result)));
  }

  public static int evaluationBoard(Board board, Color color) {
    if (board.getAllPiecePositionByColor(color).size() == 1) {
      return kingEndgameEval(
          color == Color.WHITE ? board.getWhiteKingPosition() : board.getBlackKingPosition());
    }
    return calculatePiecesPrice(board, color) + pieceSecurity(board, color);
  }

  protected static int kingEndgameEval(Position kingPos) {
    int row = kingPos.row().value();
    int col = kingPos.col().value();

    int rowCenter = row <= 3 ? 3 : 4;
    int colCenter = col <= 3 ? 3 : 4;
    int rowDistForCenter = Math.abs(row - rowCenter);
    int colDistForCenter = Math.abs(col - colCenter);

    return (rowDistForCenter + colDistForCenter) * -1000;
  }

  protected static int calculatePiecesPrice(Board board, Color color) {
    return board.getAllPiecePositionByColor(color).stream()
        .map(pos -> calculatePiecePrice(board.getPiece(pos), pos, color))
        .reduce(0, Integer::sum);
  }

  protected static int calculatePiecePrice(Piece piece, Position pos, Color color) {
    return piece.getFigureType() == FigureType.PAWN
        ? calculatePawnPrice(pos, color)
        : PIECE_PRICE.get(piece.getFigureType());
  }

  protected static int calculatePawnPrice(Position pos, Color color) {
    int row = pos.row().value();
    int col = pos.col().value();

    int rowPrice = PAWN_PRICE.get(color == Color.WHITE ? row : 7 - row);
    int centerBonus =
        col > 1 && col < 6 && row > 2 && row < 5 ? 500 - ((col % 4 + col % 3) * 100) : 0;

    return rowPrice + centerBonus;
  }

  protected static int pieceSecurity(Board board, Color color) {
    int result = 0;
    List<Position> friendlies = board.getAllPiecePositionByColor(color);
    List<Position> enemies = board.getAllPiecePositionByColor(inversColor(color));

    for (Position friendly : friendlies) {
      Piece fPiece = board.getPiece(friendly);
      for (Position enemy : enemies) {
        Piece ePiece = board.getPiece(enemy);
        FigureType promotionPiece = null;
        if (ePiece.getFigureType() == FigureType.PAWN
            && (friendly.row().value() == 0 || friendly.row().value() == 7)) {
          promotionPiece = FigureType.QUEEN;
        }

        Move move = new Move(enemy, friendly, promotionPiece);
        if (ePiece.canMove(board, move)) {
          if (isSecurity(board, friendly, color)) {
            result -=
                calculatePiecePrice(fPiece, friendly, color)
                    - calculatePiecePrice(ePiece, enemy, inversColor(color));
          } else {
            result -= calculatePiecePrice(fPiece, friendly, color);
          }
        }
      }
    }

    return result;
  }

  protected static boolean isSecurity(Board board, Position pos, Color color) {
    List<Position> friendlies = board.getAllPiecePositionByColor(color);

    for (Position friendly : friendlies) {
      Piece piece = board.getPiece(friendly);
      FigureType promotionPiece = null;
      if (piece.getFigureType() == FigureType.PAWN
          && (friendly.row().value() == 0 || friendly.row().value() == 7)) {
        promotionPiece = FigureType.QUEEN;
      }

      Move move = new Move(friendly, pos, promotionPiece);
      if (piece.canMove(board, move)) {
        return true;
      }
    }
    return false;
  }

  protected static int countDigit(int number) {
    int count = 0;
    while (number != 0) {
      number /= 10;
      count++;
    }

    return count;
  }

  public static Color inversColor(Color color) {
    return color == Color.WHITE ? Color.BLACK : Color.WHITE;
  }
}
