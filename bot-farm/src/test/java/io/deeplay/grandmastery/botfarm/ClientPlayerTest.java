package io.deeplay.grandmastery.botfarm;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ClientPlayerTest {
  private ClientPlayer clientPlayer;
  private final Move move = new Move(Position.fromString("e2"), Position.fromString("e4"), null);

  @BeforeEach
  void init() throws IOException {
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine()).thenReturn(ConversationService.serialize(new AcceptMove(move)));

    var out = Mockito.mock(BufferedWriter.class);
    clientPlayer = new ClientPlayer(Mockito.mock(), in, out, Color.WHITE);
    clientPlayer.setIn(in);
    clientPlayer.setOut(out);
  }

  @Test
  void createMove() {
    Assertions.assertEquals(move, clientPlayer.createMove());
  }

  @Test
  void answerDraw() {
    Assertions.assertFalse(clientPlayer.answerDraw());
  }
}
