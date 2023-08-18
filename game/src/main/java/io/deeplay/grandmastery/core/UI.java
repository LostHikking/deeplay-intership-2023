package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.listeners.InputListener;
import java.io.IOException;

public interface UI extends InputListener {
  GameMode selectMode() throws IOException;

  ChessType selectChessType() throws IOException;

  Color selectColor() throws IOException;

  String inputPlayerName(Color color) throws IOException;

  void showMove(PlayerInfo movePlayer);

  void showResultGame(GameState gameState);

  void printHelp() throws IOException;

  void showBoard(Board board, Color color);

  void incorrectMove();

  void close();
}
