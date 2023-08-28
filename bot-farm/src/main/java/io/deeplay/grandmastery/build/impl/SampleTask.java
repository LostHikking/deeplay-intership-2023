package io.deeplay.grandmastery.build.impl;

import io.deeplay.grandmastery.build.ITask;

public class SampleTask implements ITask {

  @Override
  public void init() {
    System.out.println("INIT FROM SomeTask");
  }
}
