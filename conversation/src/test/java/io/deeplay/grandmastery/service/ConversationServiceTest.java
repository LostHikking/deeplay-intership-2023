package io.deeplay.grandmastery.service;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameState;
import io.deeplay.grandmastery.dto.AcceptMove;
import io.deeplay.grandmastery.dto.CreateFarmGameRequest;
import io.deeplay.grandmastery.dto.CreateFarmGameResponse;
import io.deeplay.grandmastery.dto.CreateMoveFarmRequest;
import io.deeplay.grandmastery.dto.CreateMoveFarmResponse;
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
    var startGameRequest = new StartGameRequest("name", ChessType.CLASSIC, Color.WHITE, null, null);
    var startGameResponse = new StartGameResponse("board");
    var acceptMove = new AcceptMove(null);
    var sendMove = new SendMove(null);
    var resultGame = new ResultGame(GameState.DRAW, List.of("a", "b", "c"));
    var waitMove = new WaitMove();
    var wrongMove = new WrongMove();
    var createFarmGameRequest = new CreateFarmGameRequest();
    var createFarmGameResponse = new CreateFarmGameResponse();
    var createMoveFarmRequest = new CreateMoveFarmRequest();
    var createMoveFarmResponse = new CreateMoveFarmResponse();

    var startGameRequestJson = ConversationService.serialize(startGameRequest);
    var startGameResponseJson = ConversationService.serialize(startGameResponse);
    var acceptMoveJson = ConversationService.serialize(acceptMove);
    var sendMoveJson = ConversationService.serialize(sendMove);
    var resultGameJson = ConversationService.serialize(resultGame);
    var waitMoveJson = ConversationService.serialize(waitMove);
    var wrongMoveJson = ConversationService.serialize(wrongMove);
    var createFarmGameRequestJson = ConversationService.serialize(createFarmGameRequest);
    var createFarmGameResponseJson = ConversationService.serialize(createFarmGameResponse);
    var createMoveFarmRequestJson = ConversationService.serialize(createMoveFarmRequest);
    var createMoveFarmResponseJson = ConversationService.serialize(createMoveFarmResponse);

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
        () -> Assertions.assertEquals(wrongMove, ConversationService.deserialize(wrongMoveJson)),
        () ->
            Assertions.assertEquals(
                createFarmGameRequest, ConversationService.deserialize(createFarmGameRequestJson)),
        () ->
            Assertions.assertEquals(
                createFarmGameResponse,
                ConversationService.deserialize(createFarmGameResponseJson)),
        () ->
            Assertions.assertEquals(
                createMoveFarmRequest, ConversationService.deserialize(createMoveFarmRequestJson)),
        () ->
            Assertions.assertEquals(
                createMoveFarmResponse,
                ConversationService.deserialize(createMoveFarmResponseJson)));
  }

  @Test
  void deserializeWithClassTest() throws QueryException {
    var dto = new AcceptMove();
    var stringDto = ConversationService.serialize(dto);

    Assertions.assertEquals(dto, ConversationService.deserialize(stringDto, AcceptMove.class));
  }

  @Test
  void errorHandle() {
    var json = "{ab1{";

    Assertions.assertAll(
        () ->
            Assertions.assertThrows(
                QueryException.class, () -> ConversationService.deserialize(json)),
        () ->
            Assertions.assertThrows(
                QueryException.class,
                () -> ConversationService.deserialize(json, AcceptMove.class)));
  }
}
