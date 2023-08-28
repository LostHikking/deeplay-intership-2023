package io.deeplay.grandmastery.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName
public class CreateFarmGameRequest extends IDto {
  String name;
  Color color;
  ChessType chessType;
  String board;
}
