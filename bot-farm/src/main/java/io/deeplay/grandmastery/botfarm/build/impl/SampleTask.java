package io.deeplay.grandmastery.botfarm.build.impl;

import io.deeplay.grandmastery.botfarm.build.ITask;

public class SampleTask implements ITask {

  @Override
  public void init() {
    System.out.println("INIT FROM SomeTask");
  }
}
