package io.deeplay.grandmastery;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.SendAnswerDraw;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.exceptions.QueryException;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
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

  @Test
  void sendResultTest() throws IOException {
    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);

    Assertions.assertDoesNotThrow(() -> serverDao.sendResult(GameState.WHITE_WIN, List.of()));
  }

  @Test
  void sendResultWrongTest() throws IOException {
    var out = new BufferedWriter(new OutputStreamWriter(System.out, UTF_8));
    out.close();

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), out, "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);

    Assertions.assertDoesNotThrow(() -> serverDao.sendResult(GameState.WHITE_WIN, List.of()));
  }

  @Test
  void sendResultWrongTest2() throws IOException {
    var out = new BufferedWriter(new OutputStreamWriter(System.out, UTF_8));
    out.close();

    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), out, "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);

    Assertions.assertDoesNotThrow(() -> serverDao.sendResult(GameState.WHITE_WIN, List.of()));
  }

  @Test
  void notifyWrongMoveTest() throws IOException {
    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);

    Assertions.assertDoesNotThrow(() -> serverDao.notifyWrongMove(Color.WHITE));
  }

  @Test
  void notifySuccessMoveTest() throws IOException {
    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);

    Assertions.assertDoesNotThrow(
        () -> serverDao.notifySuccessMove(Color.WHITE, Mockito.mock(), new HashBoard()));
  }

  @Test
  void notifyStartGameTest() throws IOException {
    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);
    Mockito.when(socket.getOutputStream()).thenReturn(System.out);

    var board = new HashBoard();
    Boards.defaultChess().accept(board);

    Assertions.assertDoesNotThrow(() -> serverDao.notifyStartGame(board));
  }

  @Test
  void closeTest() {
    var serverPlayer =
        new ServerPlayer(
            Mockito.mock(), Mockito.mock(), Mockito.mock(), "name", Color.WHITE, ChessType.CLASSIC);
    var socket = Mockito.mock(Socket.class);
    var serverDao = new ServerDao(serverPlayer, serverPlayer, socket);

    Assertions.assertDoesNotThrow(serverDao::close);
  }
}
