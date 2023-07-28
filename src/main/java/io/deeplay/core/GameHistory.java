package io.deeplay.core;

import io.deeplay.domain.GameErrorCode;
import io.deeplay.exceptions.GameException;

import java.util.ArrayList;
import java.util.List;

public class GameHistory implements GameListener {
    private final List<Move> moves = new ArrayList<>();
    private Board board;

    @Override
    public void setBoard(Board board) {
        if (this.board == null)
            this.board = board; // TODO: Create with copy-constructor
    }

    @Override
    public void makeMove(Move move) {
        moves.add(move);
    }

    public boolean isEmpty() {
        return moves.isEmpty();
    }

    public Move getLastMove() {
        if (this.isEmpty())
            throw new GameException(GameErrorCode.MOVE_NOT_FOUND);

        return moves.get(moves.size() - 1);
    }
}
