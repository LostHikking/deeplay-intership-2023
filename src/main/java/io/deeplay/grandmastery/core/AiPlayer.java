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
    ArrayList<Piece> piecesOfThisColor = new ArrayList<>();
    ArrayList<Position> positions = new ArrayList<>();
    Piece p;
    Position pos;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        p = board.getPiece(j, i);
        pos = new Position(new Column(j), new Row(i));
        if (p != null && p.getColor() == this.color && !p.getAllMoves(board, pos).isEmpty()) {
          piecesOfThisColor.add(p);
          positions.add(pos);
        }
      }
    }
    List<Move> moves;
    if (piecesOfThisColor.isEmpty()) {
      return false;
    }
    int size = piecesOfThisColor.size();
    int ind = (int) (Math.random() * size);
    moves = piecesOfThisColor.get(ind).getAllMoves(board, positions.get(ind));
    ind = (int) (Math.random() * moves.size());
    this.setMoveData(moves.get(ind));
    return true;
  }
}
