package io.deeplay.grandmastery.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Класс для получения предложения/согласия на ничью. */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonTypeName
public class WaitAnswerDraw extends IDto {}
