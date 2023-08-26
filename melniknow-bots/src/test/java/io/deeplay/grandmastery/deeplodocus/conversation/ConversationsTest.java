package io.deeplay.grandmastery.deeplodocus.conversation;

import io.deeplay.grandmastery.deeplodocus.conversation.dto.ServerRequest;
import io.deeplay.grandmastery.deeplodocus.conversation.dto.ServerResponse;
import io.deeplay.grandmastery.deeplodocus.domain.Algorithm;
import io.deeplay.grandmastery.deeplodocus.exceptions.DeeplodocusClientException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConversationsTest {

  @Test
  void serializeAndDeserialize() throws DeeplodocusClientException {
    var request = new ServerRequest("___", Algorithm.STOCKFISH);
    var response = new ServerResponse("e2e4");

    var requestJson = Conversations.getJsonFromObject(request);
    var responseJson = Conversations.getJsonFromObject(response);

    Assertions.assertAll(
        () ->
            Assertions.assertEquals(
                request, Conversations.getObjectFromJson(requestJson, ServerRequest.class)),
        () ->
            Assertions.assertEquals(
                response, Conversations.getObjectFromJson(responseJson, ServerResponse.class)));
  }
}
