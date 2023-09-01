package io.deeplay.grandmastery.deeplodocus;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Player;
import io.deeplay.grandmastery.deeplodocus.conversation.Conversations;
import io.deeplay.grandmastery.deeplodocus.conversation.dto.ServerRequest;
import io.deeplay.grandmastery.deeplodocus.conversation.dto.ServerResponse;
import io.deeplay.grandmastery.deeplodocus.domain.Algorithm;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameErrorCode;
import io.deeplay.grandmastery.exceptions.GameException;
import io.deeplay.grandmastery.utils.BotUtils;
import io.deeplay.grandmastery.utils.LongAlgebraicNotation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DeeplodocusClient extends Player {
  private static final String HOST = "localhost";
  private static final int PORT = 6671;

  private final Algorithm algorithm;

  /**
   * Конструктор с параметрами.
   *
   * @param color Цвет
   */
  public DeeplodocusClient(Color color, Algorithm algorithm) {
    super("Deeplodocus", color);
    this.algorithm = algorithm;
  }

  @Override
  public Move createMove() throws GameException {
    var fen = BotUtils.getFenFromBoard(getBoard(), color, gameHistory);

    try (var socket = new Socket(HOST, PORT)) {
      var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
      var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

      var serverRequest = new ServerRequest(fen, algorithm);
      out.write(Conversations.getJsonFromObject(serverRequest));
      out.newLine();
      out.flush();

      var serverResponse = Conversations.getObjectFromJson(in.readLine(), ServerResponse.class);
      var stringMove = serverResponse.getMove();

      return LongAlgebraicNotation.getMoveFromString(stringMove);
    } catch (IOException e) {
      throw GameErrorCode.UNDEFINED_BEHAVIOR_BOT.asException();
    }
  }

  @Override
  public boolean answerDraw() throws GameException {
    return false;
  }
}
