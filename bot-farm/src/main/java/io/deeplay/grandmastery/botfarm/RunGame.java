package io.deeplay.grandmastery.botfarm;

import io.deeplay.grandmastery.botfarm.utils.FarmUtils;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.domain.MoveType;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.WaitAnswerDraw;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public final class RunGame implements Runnable {
  private static final long TIMEOUT_SECONDS = 5;

  private final ExecutorService executor;
  private final Player player;
  private final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;
  private final Board board;

  /**
   * Создает экземпляр класса {@code RunGame}.
   *
   * @param player Бот.
   * @param socket Сокет.
   * @param in BufferedReader.
   * @param out BufferedWriter.
   * @param board Доска.
   */
  public RunGame(Player player, Socket socket, BufferedReader in, BufferedWriter out, Board board) {
    this.executor = Executors.newSingleThreadExecutor();
    this.player = player;
    this.socket = socket;
    this.in = in;
    this.out = out;
    this.board = board;
  }

  @Override
  public void run() {
    player.startup(board);

    try {
      IDto serverDto;

      do {
        var serverResponse = FarmUtils.getJsonFromServer(in);
        serverDto = ConversationService.deserialize(serverResponse);

        if (serverDto instanceof WaitMove) {
          if (!tryMakeMoveWithinTimeLimit()) {
            log.info(player.getName() + " technical defeat because timeout");
            Move move = new Move(null, null, null, MoveType.TECHNICAL_DEFEAT);
            FarmUtils.send(out, ConversationService.serialize(new SendMove(move)));
          }
        } else if (serverDto instanceof WrongMove) {
          player.rollback();
        } else if (serverDto instanceof AcceptMove acceptMove) {
          player.makeMove(acceptMove.getMove());
        } else if (serverDto instanceof WaitAnswerDraw) {
          var json = ConversationService.serialize(new SendAnswerDraw(player.answerDraw()));
          FarmUtils.send(out, json);
        }
      } while (!(serverDto instanceof ResultGame));
    } catch (Exception e) {
      log.info("Игра завершена");
    }

    try {
      socket.close();
      executor.shutdown();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Попытка выполнить ход в рамках ограниченного времени.
   *
   * @return {@code true}, если ход выполнен, {@code false} в противном случае.
   * @throws GameException В случае возникновения ошибки в игре.
   */
  private boolean tryMakeMoveWithinTimeLimit() throws GameException {
    if (!executor.isShutdown()) {
      Runnable makeMoveTask =
          () -> {
            try {
              makeMove();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          };

      Future<?> future = executor.submit(makeMoveTask);
      try {
        future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        return true;
      } catch (TimeoutException e) {
        return false;
      } catch (InterruptedException | ExecutionException e) {
        throw GameErrorCode.ERROR_PLAYER_MAKE_MOVE.asException(e);
      }
    } else {
      throw GameErrorCode.GAME_ALREADY_OVER.asException();
    }
  }

  /**
   * Выполняет ход в игре.
   *
   * @throws IOException В случае ошибки при чтении/записи данных.
   */
  private void makeMove() throws IOException {
    Move move;
    while (true) {
      try {
        move = player.createMove();
        if (move != null) {
          if (move.moveType() == MoveType.DEFAULT) {
            player.makeMove(move);
          }
          break;
        }
      } catch (GameException e) {
        if (e.getMessage().contains(GameErrorCode.GAME_ALREADY_OVER.getDescription())) {
          log.error("Игра уже завершилась!");
          return;
        }
      }
    }

    var dto = new SendMove(move);
    FarmUtils.send(out, ConversationService.serialize(dto));
  }
}
