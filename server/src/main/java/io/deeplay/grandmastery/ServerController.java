package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import java.io.IOException;
import java.util.List;

public record ServerController(ServerDao serverDao) {
  public void notifyWrongMove(Color color) throws IOException {
    serverDao.notifyWrongMove(color);
  }

  public void notifySuccessMove(Color color, Move lastMove, Board board) throws IOException {
    serverDao.notifySuccessMove(color, lastMove, board);
  }

  public void notifyStartGame(Board board) throws IOException {
    serverDao.notifyStartGame(board);
  }

  public void sendResult(GameState gameStatus, List<Board> boardList) throws IOException {
    serverDao.sendResult(gameStatus, boardList);
  }

  /**
   * Функция закрывает ресурсы.
   *
   * @throws IOException В случае ошибки закрытия
   */
  public void close() throws IOException {
    serverDao.close();
  }
}
