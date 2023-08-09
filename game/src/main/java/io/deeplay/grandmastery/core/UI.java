package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import java.io.IOException;

public interface UI {
  GameMode selectMode() throws IOException;

  Color selectColor() throws IOException;

  String inputPlayerName(Color color) throws IOException;

  void showMove(Board board, PlayerInfo movePlayer);

  void showResultGame(PlayerInfo winPlayer);

  void printHelp() throws IOException;

  String inputMove(String playerName) throws IOException;

  void showBoard(Board board);

  void incorrectMove();

  void emptyStartPosition(Move move);

  void moveImpossible(Move move);

  void warningYourKingInCheck(Move move);

  void close();
}
