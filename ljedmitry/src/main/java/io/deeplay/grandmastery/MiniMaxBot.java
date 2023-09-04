package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.GameStateChecker;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.PlayerInfo;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.utils.Boards;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiniMaxBot {
  private static final Map<FigureType, Integer> PIECE_PRICE =
      Map.of(
          FigureType.PAWN,
          1,
          FigureType.BISHOP,
          3,
          FigureType.KNIGHT,
          3,
          FigureType.ROOK,
          5,
          FigureType.QUEEN,
          9,
          FigureType.KING,
          0);

  private final Color botColor;

  private final int deep;

  private final boolean isMax;

  private final Map<Move, Double> moveThree;

  public MiniMaxBot(PlayerInfo playerInfo, int deep) {
    this.botColor = playerInfo.getColor();
    this.isMax = true;
    this.deep = deep;
    this.moveThree = new HashMap<>();
  }

  public Move findBestMove(Board board, GameHistory gameHistory) {
    return findBestMove(board, gameHistory, botColor, this.deep, -1, 1, this.isMax);
  }

  private Move findBestMove(
      Board board,
      GameHistory gameHistory,
      Color color,
      int deep,
      double alpha,
      double beta,
      boolean isMax) {
    if (deep == 0 || isGameOver(board, color, gameHistory)) {
      double our_rate = evaluationBoard(board, gameHistory, botColor, color);
      double opponent_rate = evaluationBoard(board, gameHistory, inversColor(botColor), color);
      moveThree.put(board.getLastMove(), our_rate - opponent_rate);
      return board.getLastMove();
    }

    List<Move> allMoves = getPossibleMoves(board, color);
    Move bestMove = allMoves.get(0);

    for (Move move : allMoves) {
      double eval = moveThree.get(dfs(board, gameHistory, move, color, deep, alpha, beta, isMax));
      if (isMax) {
        if (eval > alpha) {
          alpha = eval;
          moveThree.put(move, eval);
          bestMove = move;
        }
      } else {
        if (eval < beta) {
          beta = eval;
          moveThree.put(move, eval);
          bestMove = move;
        }
      }

      if (beta <= alpha) {
        break;
      }
    }

    return bestMove;
  }

  private Move dfs(
      Board board,
      GameHistory gameHistory,
      Move move,
      Color color,
      int deep,
      double alpha,
      double beta,
      boolean isMax) {
    Board copyBoard = copyAndMove(move, board);
    GameHistory copyHistory = copyHistoryAndMove(copyBoard, gameHistory);
    Color opponentColor = inversColor(color);

    return findBestMove(copyBoard, copyHistory, opponentColor, deep - 1, alpha, beta, !isMax);
  }

  private GameHistory copyHistoryAndMove(Board board, GameHistory gameHistory) {
    GameHistory copyHistory = gameHistory.getCopy();
    copyHistory.addBoard(board);
    copyHistory.makeMove(board.getLastMove());

    return copyHistory;
  }

  private Board copyAndMove(Move move, Board board) {
    Board copyBoard = new HashBoard();
    Boards.copy(board).accept(copyBoard);

    Piece piece = copyBoard.getPiece(move.from());
    piece.move(copyBoard, move);
    copyBoard.setLastMove(move);

    return copyBoard;
  }

  private List<Move> getPossibleMoves(Board board, Color color) {
    List<Move> moves = new ArrayList<>();
    List<Position> positions = board.getAllPiecePositionByColor(color);

    for (Position position : positions) {
      moves.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return moves;
  }

  private boolean isGameOver(Board board, Color color, GameHistory gameHistory) {
    return GameStateChecker.isMate(board, color)
        || GameStateChecker.isMate(board, inversColor(color))
        || GameStateChecker.isDraw(board, gameHistory);
  }

  private double evaluationBoard(
      Board board, GameHistory gameHistory, Color mainColor, Color currentColor) {
    if (mainColor == currentColor) {
      if (GameStateChecker.isMate(board, inversColor(mainColor))) {
        return 1;
      }
    } else {
      if (GameStateChecker.isMate(board, mainColor)) {
        return -1;
      }
    }

    if (GameStateChecker.isDraw(board, gameHistory)) {
      return evaluationFunc(board, inversColor(mainColor));
    }

    return evaluationFunc(board, mainColor);
  }

  private double evaluationFunc(Board board, Color color) {
    int sumFigurePrice =
        board.getAllPiecePositionByColor(color).stream()
            .map(pos -> PIECE_PRICE.get(board.getPiece(pos).getFigureType()))
            .reduce(0, Integer::sum);

    return (double) sumFigurePrice / Math.pow(10, countDigit(sumFigurePrice));
  }

  private int countDigit(int number) {
    int count = 0;
    while (number != 0) {
      number /= 10;
      count++;
    }

    return count;
  }

  private Color inversColor(Color color) {
    return color == Color.WHITE ? Color.BLACK : Color.WHITE;
  }
}
