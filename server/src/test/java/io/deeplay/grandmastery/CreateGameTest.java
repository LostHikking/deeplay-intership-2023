package io.deeplay.grandmastery;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
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
  void createHumanVsHumanMatchTest() {
    var request = new StartGameRequest("Alex", ChessType.CLASSIC, Color.BLACK, null, null);
    var request2 = new StartGameRequest("Mike", ChessType.CLASSIC, Color.WHITE, null, null);

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
    var request = new StartGameRequest("Alex", ChessType.CLASSIC, Color.WHITE, null, null);

    var serverGame = createGame.createServerGame(request);
    Assertions.assertNull(serverGame);
  }
}
