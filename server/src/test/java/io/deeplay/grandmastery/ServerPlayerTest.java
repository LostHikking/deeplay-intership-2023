package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServerPlayerTest {
  @Test
  void createMoveSuccessTest() throws IOException {
    var move = new Move(Position.fromString("a1"), Position.fromString("b2"), null);
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine()).thenReturn(ConversationService.serialize(new SendMove(move)));

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), in, Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);

    Assertions.assertDoesNotThrow(serverPlayer::createMove);
  }

  @Test
  void createMoveWrongTest() throws IOException {
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine()).thenReturn(ConversationService.serialize(new SendMove()));

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), in, Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);

    Assertions.assertThrows(GameException.class, serverPlayer::createMove);
  }

  @Test
  void answerDrawTest() throws IOException {
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine()).thenReturn(ConversationService.serialize(new SendAnswerDraw(true)));

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), in, Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);

    Assertions.assertTrue(serverPlayer.answerDraw());
  }
}
