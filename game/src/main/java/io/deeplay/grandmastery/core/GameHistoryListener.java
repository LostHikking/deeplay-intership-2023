package io.deeplay.grandmastery.core;

public interface GameHistoryListener {
  void setBoard(Board board);

  void makeMove(Move move, Board board);
}
