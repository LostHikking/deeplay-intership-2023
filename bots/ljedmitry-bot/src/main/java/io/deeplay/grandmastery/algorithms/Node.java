package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Move;

public class Node {
  protected Move move;
  protected double eval;

  public Node(Move move, double eval) {
    this.move = move;
    this.eval = eval;
  }
}
