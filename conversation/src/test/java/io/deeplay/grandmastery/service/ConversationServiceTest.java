package io.deeplay.grandmastery.service;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.ResultGame;
import io.deeplay.grandmastery.dto.SendMove;
import io.deeplay.grandmastery.dto.StartGameRequest;
import io.deeplay.grandmastery.dto.StartGameResponse;
import io.deeplay.grandmastery.dto.WaitMove;
import io.deeplay.grandmastery.dto.WrongMove;
import io.deeplay.grandmastery.exceptions.QueryException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConversationServiceTest {
  @Test
  void serializeAndDeserialize() throws QueryException {
    var startGameRequest =
        new StartGameRequest(
            "name", ChessType.CLASSIC, Color.WHITE, null, null);
    var startGameResponse = new StartGameResponse("board");
    var acceptMove = new AcceptMove(null);
    var sendMove = new SendMove(null);
    var resultGame = new ResultGame(GameState.DRAW, List.of("a", "b", "c"));
    var waitMove = new WaitMove();
    var wrongMove = new WrongMove();

    var startGameRequestJson = ConversationService.serialize(startGameRequest);
    var startGameResponseJson = ConversationService.serialize(startGameResponse);
    var acceptMoveJson = ConversationService.serialize(acceptMove);
    var sendMoveJson = ConversationService.serialize(sendMove);
    var resultGameJson = ConversationService.serialize(resultGame);
    var waitMoveJson = ConversationService.serialize(waitMove);
    var wrongMoveJson = ConversationService.serialize(wrongMove);

    Assertions.assertAll(
        () ->
            Assertions.assertEquals(
                startGameRequest, ConversationService.deserialize(startGameRequestJson)),
        () ->
            Assertions.assertEquals(
                startGameResponse,
                ConversationService.deserialize(startGameResponseJson, StartGameResponse.class)),
        () -> Assertions.assertEquals(acceptMove, ConversationService.deserialize(acceptMoveJson)),
        () -> Assertions.assertEquals(sendMove, ConversationService.deserialize(sendMoveJson)),
        () -> Assertions.assertEquals(resultGame, ConversationService.deserialize(resultGameJson)),
        () -> Assertions.assertEquals(waitMove, ConversationService.deserialize(waitMoveJson)),
        () -> Assertions.assertEquals(wrongMove, ConversationService.deserialize(wrongMoveJson)));
  }
}
