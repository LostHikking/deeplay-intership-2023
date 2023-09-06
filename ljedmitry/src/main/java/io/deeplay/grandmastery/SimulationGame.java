package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.*;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.figures.King;
import io.deeplay.grandmastery.figures.Queen;
import io.deeplay.grandmastery.figures.Rook;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulationGame {
  private static final int COUNT_TESTS = 10;

  public static void main(String[] args) {
    Gui gui = new Gui(true);

    for (int i = 1; i <= COUNT_TESTS; i++) {
      try {
        Player bot = new LjeDmitryBot(Color.WHITE);
        Player bot1 = new HumanPlayer("Dima", Color.BLACK, gui);
        Board board = new HashBoard();
        board.setPiece(Position.fromString("e5"), new King(Color.BLACK));
        board.setPiece(Position.fromString("e1"), new King(Color.WHITE));
        board.setPiece(Position.fromString("a1"), new Rook(Color.WHITE));
        board.setPiece(Position.fromString("d1"), new Queen(Color.WHITE));

        bot.startup(board);
        bot1.startup(board);
        Game game = new Game();
        game.startup(board);
        game.setGameState(GameState.WHITE_MOVE);

        GameHistory gameHistory = new GameHistory();
        gameHistory.startup(board);
        bot.setGameHistory(gameHistory);

        boolean whiteMove = true;
        while (true) {
          Move move;
          if (whiteMove) {
            move = bot.createMove();
          } else {
            move = bot1.createMove();
          }
          game.makeMove(move);
          gameHistory.addBoard(game.getCopyBoard());
          gameHistory.makeMove(move);
          bot.makeMove(move);
          bot1.makeMove(move);

          whiteMove = !whiteMove;
          if (GameStateChecker.isMate(board, Color.BLACK)) {
            bot.gameOver(GameState.WHITE_WIN);
            bot1.gameOver(GameState.WHITE_WIN);
            game.gameOver(GameState.WHITE_WIN);
            gameHistory.gameOver(GameState.WHITE_WIN);
            System.out.println(GameState.WHITE_MOVE);
            break;
          }

          if (GameStateChecker.isMate(board, Color.WHITE)) {
            bot.gameOver(GameState.BLACK_WIN);
            bot1.gameOver(GameState.BLACK_WIN);
            game.gameOver(GameState.BLACK_WIN);
            gameHistory.gameOver(GameState.BLACK_WIN);
            System.out.println(GameState.BLACK_WIN);
            break;
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
