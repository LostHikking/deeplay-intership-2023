package io.deeplay.grandmastery.bots;

import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.domain.Color;
import java.util.Arrays;

public class BotFactory {
  /**
   * Метод создаёт игрока по имени и цвету.
   *
   * @param name Имя игрока
   * @param color Цвет
   * @return Игрок
   */
  public static Player create(String name, Color color) {
    return Arrays.stream(Bots.values())
        .filter(bot -> bot.name.equals(name))
        .findAny()
        .orElseThrow()
        .constructor
        .apply(color);
  }
}
