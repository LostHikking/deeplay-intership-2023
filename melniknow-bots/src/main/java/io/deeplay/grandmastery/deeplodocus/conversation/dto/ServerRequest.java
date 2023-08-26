package io.deeplay.grandmastery.deeplodocus.conversation.dto;

import io.deeplay.grandmastery.deeplodocus.domain.Algorithm;
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
@EqualsAndHashCode
public class ServerRequest {
  String fen;
  Algorithm algorithm;
}
