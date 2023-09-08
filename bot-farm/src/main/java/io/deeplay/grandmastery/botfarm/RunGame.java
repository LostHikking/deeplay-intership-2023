package io.deeplay.grandmastery.botfarm;

import io.deeplay.grandmastery.botfarm.utils.FarmUtils;
import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.GameController;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.dto.CreateMoveFarmResponse;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record RunGame(Player player, ClientPlayer clientPlayer, Board board) implements Runnable {
  /**
   * Конструктор.
   *
   * @param player Бот
   * @param clientPlayer Клиент-игрок
   * @param board Доска
   */
  public RunGame(Player player, ClientPlayer clientPlayer, Board board) {
    this.player = player;
    this.clientPlayer = clientPlayer;
    this.board = board;
    System.out.println(board);
  }

  @Override
  public void run() {
    var gameController = new GameController(player, clientPlayer);
    gameController.beginPlay(board);

    while (!gameController.isGameOver()) {
      gameController.nextMove();
      log.info("Сделан ход цветом - " + gameController.getOpponentPlayer().getColor());

      if (gameController.getCurrentPlayer().getColor() == clientPlayer.getColor()
          || gameController.isGameOver()) {
        try {
          var dto = new CreateMoveFarmResponse(gameController.getGameHistory().getLastMove());
          FarmUtils.send(clientPlayer.getOut(), ConversationService.serialize(dto));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    log.info("Игра завершена");

    try {
      clientPlayer.getSocket().close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
