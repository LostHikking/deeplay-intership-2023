package io.deeplay.grandmastery.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = StartGameResponseDto.class),
  @JsonSubTypes.Type(value = StartGameRequestDto.class),
  @JsonSubTypes.Type(value = ErrorDtoResponse.class)
})
public class IDto {
  public IDto() {}
}
