package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.FigureType;

public record Move(Position from, Position to, FigureType promotionPiece) {
}
