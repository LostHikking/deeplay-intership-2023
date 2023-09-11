package io.deeplay.grandmastery.botfarm.utils;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.service.ConversationService;
import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FarmUtilsTest {

  @Test
  void getJsonFromServer() throws IOException {
    var sendMove =
        new SendMove(new Move(Position.fromString("a1"), Position.fromString("b2"), null));
    var bufferedReader = Mockito.mock(BufferedReader.class);
    Mockito.when(bufferedReader.readLine()).thenReturn(ConversationService.serialize(sendMove));

    var json = FarmUtils.getJsonFromServer(bufferedReader);
    Assertions.assertEquals(ConversationService.serialize(sendMove), json);
  }
}
