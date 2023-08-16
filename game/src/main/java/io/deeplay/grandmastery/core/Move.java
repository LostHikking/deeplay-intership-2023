package io.deeplay.grandmastery.core;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.deeplay.grandmastery.domain.FigureType;
import io.deeplay.grandmastery.domain.MoveType;

@JsonSerialize
public record Move(Position from, Position to, FigureType promotionPiece, MoveType moveType) {
  public Move(Position from, Position to, FigureType promotionPiece) {
    this(from, to, promotionPiece, MoveType.DEFAULT);
  }
}
