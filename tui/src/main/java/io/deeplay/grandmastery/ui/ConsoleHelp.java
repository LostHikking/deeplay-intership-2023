package io.deeplay.grandmastery.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ConsoleHelp {
  public static final String help;

  static {
    String temp;

    var stringBuilder = new StringBuilder("\n");

    try (var file =
        new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(ConsoleHelp.class.getResourceAsStream("/Help.txt")),
                StandardCharsets.UTF_8))) {
      file.lines().forEach(s -> stringBuilder.append(s).append("\n"));
      temp = stringBuilder.toString();
    } catch (IOException e) {
      temp = "";
    }

    help = temp;
  }
}
