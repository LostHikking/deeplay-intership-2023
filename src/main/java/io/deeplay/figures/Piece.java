package io.deeplay.figures;

import io.deeplay.core.Board;
import io.deeplay.domain.Color;
import io.deeplay.core.Move;
import io.deeplay.core.Position;

import java.util.List;

public abstract class Piece {
    private final Color color;
    private boolean isMoved;

    public Piece(Color color) {
        this.color = color;
        isMoved = false;
    }

    public Color getColor() {
        return color;
    }

    public boolean isMoved() {
        return isMoved;
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }


    public abstract void move(Position from, Position to, Board board);

    public abstract boolean canMove(Position from, Position to, Board board);

    public abstract List<Move> getAllMoves(Board board);

    public abstract void revive(Position position, Board board, Piece piece);

    public abstract boolean canRevive(Position position, Board board);
}
