package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.exceptions.QueryException;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServerDaoTest {
  private ServerPlayer serverPlayer;
  private final BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);

  @BeforeEach
  void init() {
    serverPlayer =
        new ServerPlayer(
            Mockito.mock(Socket.class),
            bufferedReader,
            Mockito.mock(BufferedWriter.class),
            "Alex",
            Color.WHITE,
            GameMode.HUMAN_VS_BOT,
            ChessType.CLASSIC);
  }

  @Test
  void sendMoveTest() throws IOException {
    var dto = new SendMove(new Move(Position.fromString("a1"), Position.fromString("b2"), null));
    try (var utilities = Mockito.mockStatic(ServerDao.class)) {
      utilities
          .when(() -> ServerDao.getJsonFromClient(bufferedReader))
          .thenReturn(ConversationService.serialize(dto));

      var move = serverPlayer.createMove();
      Assertions.assertEquals(dto.getMove(), move);
    }
  }

  @Test
  void answerDrawTest() throws QueryException {
    try (var utilities = Mockito.mockStatic(ServerDao.class)) {
      utilities
          .when(() -> ServerDao.getJsonFromClient(bufferedReader))
          .thenReturn(ConversationService.serialize(new SendAnswerDraw(true)));
      Assertions.assertTrue(serverPlayer.answerDraw());
    }
  }

  @Test
  void getJsonFromClientOneLineTest() throws IOException {
    var ex = "some_json";
    var inputStream =
        new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(ex.getBytes(UTF_8)), UTF_8));
    var json = ServerDao.getJsonFromClient(inputStream);

    Assertions.assertEquals(ex, json);
  }

  @Test
  void getJsonFromClientMultiplyLineTest() throws IOException {
    var ex = """
    some_json_with_multiply
    lines___""";
    var inputStream =
        new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(ex.getBytes(UTF_8)), UTF_8));
    var json = ServerDao.getJsonFromClient(inputStream);

    Assertions.assertEquals(ex, json);
  }
}
