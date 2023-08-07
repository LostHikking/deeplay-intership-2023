package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import java.util.ArrayList;
import java.util.List;

public class AiPlayer extends Player {
  public AiPlayer(Board board, Color color) {
    super("Bot", board, color);
  }

  @Override
  public void makeMove() {
    List<Move> possibleMove = new ArrayList<>();
    List<Position> positions = board.getAllPieceByColorPosition(this.color);
    for (Position position : positions) {
      possibleMove.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    if (possibleMove.isEmpty()) {
      setMoveData((String) null);
      return;
    }
    int ind = (int) (Math.random() * possibleMove.size());
    this.setMoveData(possibleMove.get(ind));
  }
}
