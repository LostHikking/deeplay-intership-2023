package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;

public class AiPlayer extends Player {
  public AiPlayer(Board board, Color color) {
    super("Bot", board, color, new EmptyUi());
  }

  public AiPlayer(String name, Board board, Color color, UI ui) {
    super(name, board, color, ui);
  }

  @Override
  public void makeMove(Board board) throws GameException {
    this.board = board;

    List<Move> possibleMove = new ArrayList<>();
    List<Position> positions = board.getAllPieceByColorPosition(this.color);

    for (Position position : positions) {
      possibleMove.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    if (possibleMove.isEmpty()) {
      setMoveData("");
      throw GameErrorCode.UNDEFINED_BEHAVIOR_BOT.asException();
    }
    int ind = (int) (Math.random() * possibleMove.size());
    this.setMoveData(possibleMove.get(ind));
  }
}
