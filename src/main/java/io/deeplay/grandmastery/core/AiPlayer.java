package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.List;

public class AiPlayer extends Player {
  public AiPlayer(Board board, Color color) {
    super("Bot", board, color);
  }


  @Override
  public boolean makeMove() {
    ArrayList<Piece> piecesWithMoves = new ArrayList<>();
    List<Position> positions;
    // Создаем список позиций для удаления
    List<Position> positionsToRemove = new ArrayList<>();
    Piece p;
    if (this.color == Color.BLACK) {
      positions = board.getAllBlackPiecePosition();
    } else {
      positions = board.getAllWhitePiecePosition();
    }
    for (Position position : positions) {
      p = board.getPiece(position);
      if (!p.getAllMoves(board, position).isEmpty()) {
        piecesWithMoves.add(p);
      } else {
        positionsToRemove.add(position);
      }
    }
    //удаляю их вне цикла, чтобы не было предупреждений об ConcurrentModificationException
    positions.removeAll(positionsToRemove);
    if (piecesWithMoves.isEmpty()) {
      return false;
    }
    //занимаемся рандомом
    List<Move> moves;
    int size = piecesWithMoves.size();
    int ind = (int) (Math.random() * size);
    moves = piecesWithMoves.get(ind).getAllMoves(board, positions.get(ind));
    ind = (int) (Math.random() * moves.size());
    this.setMoveData(moves.get(ind));
    return true;
  }
}
