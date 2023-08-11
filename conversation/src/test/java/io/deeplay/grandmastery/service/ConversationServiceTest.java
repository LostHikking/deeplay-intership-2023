// package io.deeplay.grandmastery.service;
//
// import io.deeplay.grandmastery.domain.ChessType;
// import io.deeplay.grandmastery.domain.Color;
// import io.deeplay.grandmastery.domain.GameMode;
// import io.deeplay.grandmastery.dto.ErrorDtoResponse;
// import io.deeplay.grandmastery.dto.StartGameRequestDto;
// import io.deeplay.grandmastery.dto.StartGameResponseDto;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// class ConversationServiceTest {
//  private ErrorDtoResponse errorDtoResponse;
//  private StartGameResponseDto startGameResponseDto;
//  private StartGameRequestDto startGameRequestDto;
//
//  @BeforeEach
//  void init() {
//    errorDtoResponse = new ErrorDtoResponse("some desc");
//    startGameResponseDto = new StartGameResponseDto(1, 1, "token", "board");
//    startGameRequestDto =
//        new StartGameRequestDto("name", GameMode.BOT_VS_BOT, ChessType.CLASSIC, Color.WHITE);
//  }
//
//  @Test
//  void serialize() {
//    Assertions.assertAll(
//        () ->
//            Assertions.assertEquals(
//                "{\"type\":\"ErrorDtoResponse\"," + "\"description\":\"some desc\"}",
//                ConversationService.serialize(errorDtoResponse)),
//        () ->
//            Assertions.assertEquals(
//                "{\"type\":\"StartGameResponseDto\",\"gameId\":1,\"playerId\":1,"
//                    + "\"authToken\":\"token\",\"board\":\"board\"}",
//                ConversationService.serialize(startGameResponseDto)),
//        () ->
//            Assertions.assertEquals(
//                "{\"type\":\"StartGameRequestDto\",\"playerName\":\"name\","
//                    +
// "\"gameMode\":\"BOT_VS_BOT\",\"chessType\":\"CLASSIC\",\"color\":\"WHITE\"}",
//                ConversationService.serialize(startGameRequestDto)));
//  }
//
//  @Test
//  void deserializeTest() {
//    Assertions.assertAll(
//        () ->
//            Assertions.assertEquals(
//                errorDtoResponse,
//                ConversationService.deserialize(
//                    "{\"type\":\"ErrorDtoResponse\",\"description\":\"some desc\"}")),
//        () ->
//            Assertions.assertEquals(
//                startGameResponseDto,
//                ConversationService.deserialize(
//                    "{\"type\":\"StartGameResponseDto\",\"gameId\":1,\"playerId\":1,"
//                        + "\"authToken\":\"token\",\"board\":\"board\"}")),
//        () ->
//            Assertions.assertEquals(
//                startGameRequestDto,
//                ConversationService.deserialize(
//                    "{\"type\":\"StartGameRequestDto\",\"playerName\":\"name\",\"gameMode"
//                        + "\":\"BOT_VS_BOT\",\"chessType\":\"CLASSIC\",\"color\":\"WHITE\"}")));
//  }
//
//  @Test
//  void deserializeSecondTest() {
//    Assertions.assertAll(
//        () ->
//            Assertions.assertEquals(
//                errorDtoResponse,
//                ConversationService.deserialize(
//                    "{\"type\":\"ErrorDtoResponse\",\"description\":\"some desc\"}",
//                    ErrorDtoResponse.class)),
//        () ->
//            Assertions.assertEquals(
//                startGameResponseDto,
//                ConversationService.deserialize(
//                    "{\"type\":\"StartGameResponseDto\",\"gameId\":1,"
//                        + "\"playerId\":1,\"authToken\":\"token\",\"board\":\"board\"}",
//                    StartGameResponseDto.class)),
//        () ->
//            Assertions.assertEquals(
//                startGameRequestDto,
//                ConversationService.deserialize(
//                    "{\"type\":\"StartGameRequestDto\",\"playerName\":\"name\",\"gameMode"
//                        + "\":\"BOT_VS_BOT\",\"chessType\":\"CLASSIC\",\"color\":\"WHITE\"}",
//                    StartGameRequestDto.class)));
//  }
// }
