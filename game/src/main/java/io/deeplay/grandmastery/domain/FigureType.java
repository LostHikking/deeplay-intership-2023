package io.deeplay.grandmastery.domain;

import io.deeplay.grandmastery.figures.Bishop;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Knight;
import io.deeplay.grandmastery.figures.Pawn;
import io.deeplay.grandmastery.figures.Piece;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;

public enum FigureType {
  KING('k'),
  QUEEN('q'),
  ROOK('r'),
  BISHOP('b'),
  KNIGHT('n'),
  PAWN('p');

  private final char symbol;

  FigureType(char symbol) {
    this.symbol = symbol;
  }

  public char getSymbol() {
    return symbol;
  }

  /**
   * Функция возвращает фигуру по FigureType.
   * @param color Цвет
   * @return Фигура
   * */
  public Piece getPiece(Color color) {
    return switch (this) {
      case KING -> new King(color);
      case KNIGHT -> new Knight(color);
      case ROOK -> new Rook(color);
      case BISHOP -> new Bishop(color);
      case QUEEN -> new Queen(color);
      case PAWN -> new Pawn(color);
    };
  }
}
