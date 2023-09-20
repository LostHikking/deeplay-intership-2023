package io.deeplay.grandmastery.algorithms;

import io.deeplay.grandmastery.core.Move;

/** Класс, представляющий узел дерева игры, содержащий ход и его оценку. */
public class Node {
  protected Move move;
  protected double eval;

  public Node(Move move, double eval) {
    this.move = move;
    this.eval = eval;
  }
}
