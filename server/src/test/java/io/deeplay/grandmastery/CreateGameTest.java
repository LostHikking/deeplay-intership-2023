package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.dto.StartGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CreateGameTest {
  private final CreateGame createGame =
      new CreateGame(Mockito.mock(), Mockito.mock(), Mockito.mock());

  @BeforeEach
  void clear() {
    createGame.clearPlayers();
  }

  @Test
  void createBotVsBotTest() {
    var request = new StartGameRequest("Alex", GameMode.BOT_VS_BOT, ChessType.CLASSIC, Color.WHITE);
    var serverGame = createGame.createServerGame(request);

    Assertions.assertAll(
        () ->
            Assertions.assertEquals(
                AiPlayer.class,
                serverGame.getServerController().serverDao().playerOne().getClass()),
        () ->
            Assertions.assertEquals(
                AiPlayer.class,
                serverGame.getServerController().serverDao().playerTwo().getClass()));
  }

  @Test
  void createHumanVsBotTest() {
    var request =
        new StartGameRequest("Alex", GameMode.HUMAN_VS_BOT, ChessType.CLASSIC, Color.WHITE);

    var serverGame = createGame.createServerGame(request);
    Assertions.assertAll(
        () ->
            Assertions.assertEquals(
                ServerPlayer.class,
                serverGame.getServerController().serverDao().playerOne().getClass()),
        () ->
            Assertions.assertEquals(
                AiPlayer.class,
                serverGame.getServerController().serverDao().playerTwo().getClass()));
  }

  @Test
  void createHumanVsHumanMatchTest() {
    var request =
        new StartGameRequest("Alex", GameMode.HUMAN_VS_HUMAN, ChessType.CLASSIC, Color.BLACK);
    var request2 =
        new StartGameRequest("Mike", GameMode.HUMAN_VS_HUMAN, ChessType.CLASSIC, Color.WHITE);

    var serverGame = createGame.createServerGame(request);
    var serverGame2 = createGame.createServerGame(request2);

    Assertions.assertAll(
        () -> Assertions.assertNull(serverGame),
        () ->
            Assertions.assertEquals(
                ServerPlayer.class,
                serverGame2.getServerController().serverDao().playerOne().getClass()),
        () ->
            Assertions.assertEquals(
                ServerPlayer.class,
                serverGame2.getServerController().serverDao().playerTwo().getClass()));
  }

  @Test
  void createHumanVsHumanTest() {
    var request =
        new StartGameRequest("Alex", GameMode.HUMAN_VS_HUMAN, ChessType.CLASSIC, Color.WHITE);

    var serverGame = createGame.createServerGame(request);
    Assertions.assertNull(serverGame);
  }
}
