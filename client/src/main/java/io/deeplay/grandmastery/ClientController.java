package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.UI;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.exceptions.QueryException;
import java.io.IOException;

public record ClientController(ClientDao clientDao, UI ui) {

  public GameMode selectMode() throws IOException {
    return ui.selectMode();
  }

  public Color selectColor() throws IOException {
    return ui.selectColor();
  }

  public String inputPlayerName(Color color) throws IOException {
    return ui.inputPlayerName(color);
  }

  public ChessType selectChessType() throws IOException {
    return ui.selectChessType();
  }

  public IDto query(IDto request) throws QueryException {
    return clientDao.query(request);
  }

  public void showBoard(Board boardFromString, Color color) {
    ui.showBoard(boardFromString, color);
  }

  public void showResultGame(GameState gameState) {
    ui.showResultGame(gameState);
  }

  public void printHelp() throws IOException {
    ui.printHelp();
  }

  public String getJsonFromServer() throws IOException {
    return clientDao.getJsonFromServer();
  }

  public void incorrectMove() {
    ui.incorrectMove();
  }

  public void close() throws IOException {
    clientDao.close();
  }

  public void send(String json) throws IOException {
    clientDao.send(json);
  }

  public String inputMove(String name) throws IOException {
    return ui.inputMove(name);
  }
}
