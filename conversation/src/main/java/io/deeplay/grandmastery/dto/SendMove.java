package io.deeplay.grandmastery.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.grandmastery.core.Move;
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
public class SendMove extends IDto {
  Move move;
}
