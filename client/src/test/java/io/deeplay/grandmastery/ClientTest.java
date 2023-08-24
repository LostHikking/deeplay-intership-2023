package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.AiPlayer;
import io.deeplay.grandmastery.core.GameHistory;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ClientTest {
  private ClientDao clientDao;
  private final SendMove sendMove =
      new SendMove(new Move(Position.fromString("a1"), Position.fromString("b2"), null));

  @BeforeEach
  void init() throws IOException {
    var bufferedReader = Mockito.mock(BufferedReader.class);
    Mockito.when(bufferedReader.readLine()).thenReturn(ConversationService.serialize(sendMove));

    clientDao = new ClientDao(Mockito.mock(), bufferedReader, Mockito.mock());
  }

  @Test
  void queryTest() throws IOException {
    var dto = clientDao.query(new WaitMove());
    Assertions.assertEquals(sendMove, dto);
  }

  @Test
  void getJsonFromServerTest() throws IOException {
    var json = clientDao.getJsonFromServer();
    Assertions.assertEquals(ConversationService.serialize(sendMove), json);
  }

  @Test
  void makeMoveTest()
      throws NoSuchMethodException,
          InvocationTargetException,
          IllegalAccessException,
          NoSuchFieldException {
    var field = Client.class.getDeclaredField("player");
    field.setAccessible(true);

    var player = new AiPlayer(Color.WHITE);
    var board = new HashBoard();
    Boards.defaultChess().accept(board);
    player.startup(board);

    var client = Mockito.mock(Client.class);
    field.set(client, player);

    var gameHistoryField = Client.class.getDeclaredField("gameHistory");
    gameHistoryField.setAccessible(true);
    gameHistoryField.set(client, new GameHistory());

    var method = Client.class.getDeclaredMethod("makeMove");
    method.setAccessible(true);

    var move = (Move) method.invoke(client);

    Assertions.assertNotNull(move);
  }

  @Test
  void botVsBotTest()
      throws IllegalAccessException, NoSuchFieldException, IOException, NoSuchMethodException {
    var request = new StartGameRequest("AI", GameMode.BOT_VS_BOT, ChessType.CLASSIC, Color.WHITE);
    var clientController = Mockito.mock(ClientController.class);
    Mockito.when(clientController.selectMode()).thenReturn(GameMode.BOT_VS_BOT);
    Mockito.when(clientController.selectChessType()).thenReturn(ChessType.CLASSIC);
    Mockito.when(clientController.query(request))
        .thenReturn(new ResultGame(GameState.WHITE_WIN, List.of("")));

    var field = Client.class.getDeclaredField("clientController");
    field.setAccessible(true);

    var method = Client.class.getDeclaredMethod("run");
    method.setAccessible(true);

    var client = Mockito.mock(Client.class);
    field.set(client, clientController);

    Assertions.assertDoesNotThrow(() -> method.invoke(client));
  }
}
