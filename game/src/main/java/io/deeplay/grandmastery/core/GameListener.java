package io.deeplay.grandmastery.core;

public interface GameListener {
  void startup(Board board);

  void makeMove(Move move, Board board);

  void rollback(Board board);
}
