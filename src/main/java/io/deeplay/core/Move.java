package io.deeplay.core;

import io.deeplay.domain.FigureType;

public record Move(Position from, Position to, FigureType promotionPiece) {
}
