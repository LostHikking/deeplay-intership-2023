package io.deeplay.grandmastery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.slf4j.LoggerFactory.getILoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

public class GuiAppenderTest {
  private Gui mockedGui;
  private GuiAppender guiAppender;

  /** Инициализатор для тестов. */
  public void init() {
    mockedGui = mock(Gui.class);
    guiAppender = new GuiAppender(mockedGui);
    guiAppender.start();
  }

  @Test
  public void appendLogsFromClientTest() {
    init();
    Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger("io.deeplay.grandmastery.Client");

    LoggerContext loggerContext = (LoggerContext) getILoggerFactory();
    loggerContext.reset();
    logger.addAppender(guiAppender);
    logger.setAdditive(false);

    logger.info("Test message");

    verify(mockedGui, times(1)).addLog("Test message");
  }

  @Test
  public void appendLogsFromOtherClassesTest() {
    init();
    Logger logger = (Logger) LoggerFactory.getLogger("io.deeplay.someotherpackage.SomeClass");

    LoggerContext loggerContext = (LoggerContext) getILoggerFactory();
    loggerContext.reset();
    logger.addAppender(guiAppender);
    logger.setAdditive(false);

    logger.info("Test message");

    verify(mockedGui, never()).addLog(Mockito.anyString());
  }
}
