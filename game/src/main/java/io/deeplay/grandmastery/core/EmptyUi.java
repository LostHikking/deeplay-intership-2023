package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;

public class EmptyUi implements UI {

  @Override
  public GameMode selectMode() {
    return null;
  }

  @Override
  public Color selectColor() {
    return null;
  }

  @Override
  public String inputPlayerName(Color color) {
    return null;
  }

  @Override
  public void showMove(Board board, PlayerInfo movePlayer) {}

  @Override
  public void showResultGame(PlayerInfo winPlayer) {}

  @Override
  public void printHelp() {}

  @Override
  public String inputMove(String playerName) {
    return null;
  }

  @Override
  public void showBoard(Board board) {}

  @Override
  public void incorrectMove() {}

  @Override
  public void emptyStartPosition(Move move) {}

  @Override
  public void moveImpossible(Move move) {}

  @Override
  public void warningYourKingInCheck(Move move) {}

  @Override
  public void close() {}
}
