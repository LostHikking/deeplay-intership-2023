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
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        p = board.getPiece(j, i);
        if (p != null && p.getColor() == this.color) {
          piecesOfThisColor.add(p);
          positions.add(new Position(new Column(j), new Row(i)));
        }
      }
    }
    List<Move> moves;
    do {
      int i = (int) (Math.random() * piecesOfThisColor.size());
      moves = piecesOfThisColor.get(i).getAllMoves(board, positions.get(i));
    } while (moves.isEmpty());
    int i = (int) (Math.random() * moves.size());
    this.setMoveData(moves.get(i));
    return true;
  }
}
