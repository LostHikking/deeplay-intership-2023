package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;

public class AiPlayer extends Player {
  public AiPlayer(Color color) {
    super("Bot", color);
  }

  @Override
  public Move createMove() throws GameException {
    if (this.isGameOver()) {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }

    Board board = this.getBoard();
    List<Move> possibleMove = new ArrayList<>();
    List<Position> positions = board.getAllPieceByColorPosition(this.getColor());

    for (Position position : positions) {
      possibleMove.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    if (possibleMove.isEmpty()) {
      throw GameErrorCode.UNDEFINED_BEHAVIOR_BOT.asException();
    }
    int ind = (int) (Math.random() * possibleMove.size());
    this.setLastMove(possibleMove.get(ind));
    return possibleMove.get(ind);
  }

  @Override
  public boolean answerDraw() {
    return false;
  }
}
