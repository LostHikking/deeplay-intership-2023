package io.deeplay.grandmastery.deeplodocus.exceptions;

import java.io.IOException;

public class DeeplodocusClientException extends IOException {

  public DeeplodocusClientException(String message) {
    super(message);
  }

  public DeeplodocusClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
