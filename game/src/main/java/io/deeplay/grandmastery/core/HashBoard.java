package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.figures.Piece;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс, представляющий шахматную доску. Расширяет абстрактный класс Board и реализует его
 * абстрактные методы. Хранит фигуры на доске в hashMap, и обеспечивает доступ к ним по позициям.
 */
public class HashBoard extends Board {
  /** Мапа, хранящая фигуры на доске, где ключ - позиция, значение - фигура. */
  private final Map<Position, Piece> pieces;

  /** Позиция черного короля на доске. */
  private Position blackKing;

  /** Позиция белого короля на доске. */
  private Position whiteKing;

  /**
   * Создает новый экземпляр шахматной доски. Инициализирует мапу для хранения фигур и устанавливает
   * позиции королей в значение null.
   */
  public HashBoard() {
    this.pieces = new HashMap<>();
    this.blackKing = null;
    this.whiteKing = null;
  }

  @Override
  public void setPiece(Position position, Piece piece) {
    pieces.put(position, piece);
    if (piece.getFigureType() == FigureType.KING) {
      if (piece.getColor() == Color.WHITE) {
        whiteKing = position;
      } else if (piece.getColor() == Color.BLACK) {
        blackKing = position;
      }
    }
  }

  @Override
  public Piece getPiece(Position position) {
    return pieces.get(position);
  }

  @Override
  public void removePiece(Position position) {
    pieces.remove(position);
  }

  @Override
  public Position getBlackKingPosition() {
    return blackKing;
  }

  @Override
  public Position getWhiteKingPosition() {
    return whiteKing;
  }

  @Override
  public boolean hasPiece(Position position) {
    return pieces.containsKey(position);
  }

  @Override
  public List<Position> getAllPieceByColorPosition(Color color) {
    return pieces.entrySet().stream()
        .filter(entry -> entry.getValue().getColor() == color)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  @Override
  public List<Position> getAllPiecePosition() {
    return pieces.keySet().stream().toList();
  }

  @Override
  public void clear() {
    super.clear();

    pieces.clear();
    blackKing = null;
    whiteKing = null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HashBoard hashBoard)) {
      return false;
    }
    return Objects.equals(pieces, hashBoard.pieces)
        && Objects.equals(blackKing, hashBoard.blackKing)
        && Objects.equals(whiteKing, hashBoard.whiteKing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pieces, blackKing, whiteKing);
  }
}
