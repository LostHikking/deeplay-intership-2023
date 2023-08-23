package io.deeplay.grandmastery;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ClientTest {
  private ClientDao clientDao;
  private final SendMove sendMove =
      new SendMove(
          new Move(
              Position.getPositionFromString("a1"), Position.getPositionFromString("b2"), null));

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
}
