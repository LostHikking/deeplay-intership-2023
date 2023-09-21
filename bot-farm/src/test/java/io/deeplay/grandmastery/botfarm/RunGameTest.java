package io.deeplay.grandmastery.botfarm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.deeplay.grandmastery.botfarm.utils.FarmUtils;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Randomus;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.service.ConversationService;
import io.deeplay.grandmastery.utils.Boards;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

class RunGameTest {

  @BeforeAll
  public static void offLoggers() {
    Logger farmUtilsLogger = (Logger) LoggerFactory.getLogger(FarmUtils.class);
    farmUtilsLogger.setLevel(Level.OFF);
  }

  @Test
  void runTest() throws IOException {
    var player = new Randomus(Color.WHITE);
    var in = Mockito.mock(BufferedReader.class);
    Mockito.when(in.readLine())
        .thenReturn(ConversationService.serialize(new WaitMove()))
        .thenReturn(ConversationService.serialize(new ResultGame(GameState.WHITE_MOVE, List.of())));

    var board = new HashBoard();
    Boards.defaultChess().accept(board);

    var runGame = new RunGame(player, Mockito.mock(), in, Mockito.mock(), board);
    Assertions.assertDoesNotThrow(runGame::run);
  }
}
