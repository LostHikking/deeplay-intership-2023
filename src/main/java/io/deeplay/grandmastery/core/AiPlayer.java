package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.figures.Piece;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AiPlayer extends Player {
  public AiPlayer(Board board, Color color) {
    super("Bot", board, color);
  }


  @Override
  public boolean makeMove() {
    List<Piece> piecesWithMoves = new ArrayList<>();
    List<Position> positions;
    // Создаем список позиций для удаления
    Piece p;
    if (this.color == Color.BLACK) {
      positions = new ArrayList<>(board.getAllBlackPiecePosition());
    } else {
      positions = new ArrayList<>(board.getAllWhitePiecePosition());
    }
    Iterator<Position> iterator = positions.iterator();
    while (iterator.hasNext()) {
      Position position = iterator.next();
      p = board.getPiece(position);
      if (!p.getAllMoves(board, position).isEmpty()) {
        piecesWithMoves.add(p);
      } else {
        iterator.remove();
      }
    }
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
