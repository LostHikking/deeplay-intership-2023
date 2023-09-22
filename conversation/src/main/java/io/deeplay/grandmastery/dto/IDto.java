package io.deeplay.grandmastery.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Базовый класс для DTO. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = StartGameResponse.class),
  @JsonSubTypes.Type(value = StartGameRequest.class),
  @JsonSubTypes.Type(value = ResultGame.class),
  @JsonSubTypes.Type(value = WaitMove.class),
  @JsonSubTypes.Type(value = SendMove.class),
  @JsonSubTypes.Type(value = WrongMove.class),
  @JsonSubTypes.Type(value = AcceptMove.class),
  @JsonSubTypes.Type(WaitAnswerDraw.class),
  @JsonSubTypes.Type(SendAnswerDraw.class),
  @JsonSubTypes.Type(CreateFarmGameRequest.class),
  @JsonSubTypes.Type(CreateFarmGameResponse.class),
  @JsonSubTypes.Type(GetListBotsFromFarm.class),
  @JsonSubTypes.Type(ErrorConnectionBotFarm.class),
  @JsonSubTypes.Type(SendListBots.class),
  @JsonSubTypes.Type(SendBoard.class)
})
public abstract class IDto {
  public IDto() {}
}
