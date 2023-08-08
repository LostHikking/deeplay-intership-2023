package io.deeplay.grandmastery.exceptions;

import java.io.IOException;

public class QueryException extends IOException {
  public QueryException(String message) {
    super(message);
  }
}
