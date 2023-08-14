package io.deeplay.grandmastery.service;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import io.deeplay.grandmastery.dto.ErrorDtoResponse;
import io.deeplay.grandmastery.dto.StartGameRequestDto;
import io.deeplay.grandmastery.dto.StartGameResponseDto;
import io.deeplay.grandmastery.exceptions.QueryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConversationServiceTest {
  @Test
  void serializeAndDeserialize() throws QueryException {
    var errorDto = new ErrorDtoResponse("some");
    var startGameRequest =
        new StartGameRequestDto("name", GameMode.BOT_VS_BOT, ChessType.CLASSIC, Color.WHITE);
    var startGameResponse = new StartGameResponseDto("board");

    var errorDtoJson = ConversationService.serialize(errorDto);
    var startGameRequestJson = ConversationService.serialize(startGameRequest);
    var startGameResponseJson = ConversationService.serialize(startGameResponse);

    Assertions.assertAll(
        () -> Assertions.assertEquals(errorDto, ConversationService.deserialize(errorDtoJson)),
        () ->
            Assertions.assertEquals(
                startGameRequest, ConversationService.deserialize(startGameRequestJson)),
        () ->
            Assertions.assertEquals(
                startGameResponse, ConversationService.deserialize(startGameResponseJson)));
  }
}
