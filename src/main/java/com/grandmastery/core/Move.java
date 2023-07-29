package com.grandmastery.core;

import com.grandmastery.domain.FigureType;

public record Move(Position from, Position to, FigureType promotionPiece) {
}
