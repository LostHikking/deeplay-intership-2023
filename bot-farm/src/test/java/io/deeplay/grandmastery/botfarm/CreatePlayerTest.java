package io.deeplay.grandmastery.botfarm;

import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.CreateFarmGameRequest;
import io.deeplay.grandmastery.dto.GetListBotsFromFarm;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreatePlayerTest {
  private CreatePlayer createPlayer;
  private final CreateFarmGameRequest dto =
      new CreateFarmGameRequest(
          "Randomus", Color.WHITE, ChessType.CLASSIC, Boards.getString(new HashBoard()));

  @BeforeEach
  void init() throws IOException {
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine()).thenReturn(ConversationService.serialize(dto));

    var out = Mockito.mock(BufferedWriter.class);
    createPlayer = new CreatePlayer(Mockito.mock(), in, out);
  }

  @Test
  void getGame() {
    var game = createPlayer.getGame(dto);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(game),
        () -> Assertions.assertEquals("Randomus", game.player().getName()),
        () -> Assertions.assertEquals(Color.WHITE, game.player().getColor()));
  }

  @Test
  void runTest() throws IOException {
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine()).thenReturn(ConversationService.serialize(dto));

    var out = Mockito.mock(BufferedWriter.class);
    var createPlayer = new CreatePlayer(Mockito.mock(), in, out);

    Assertions.assertDoesNotThrow(createPlayer::run);
  }

  @Test
  void runTest2() throws IOException {
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine())
        .thenReturn(ConversationService.serialize(new GetListBotsFromFarm()));

    var out = Mockito.mock(BufferedWriter.class);
    var createPlayer = new CreatePlayer(Mockito.mock(), in, out);

    Assertions.assertDoesNotThrow(createPlayer::run);
  }
}
