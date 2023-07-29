package io.deeplay.grandmastery.core;

public interface GameListener {
    void setBoard(Board board);
    void makeMove(Move move);
}
