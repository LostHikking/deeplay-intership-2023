package io.deeplay.grandmastery;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/** Настраиваемый аппендер logback для перенаправления клиентских логов. */
public class GuiAppender extends AppenderBase<ILoggingEvent> {
  private Gui ui;
  private Filter<ILoggingEvent> filter;

  public GuiAppender(Gui ui) {
    this.ui = ui;
    this.filter = new LoggerFilter();
  }

  /**
   * Переопределяем метод для фильтрации логов.
   *
   * @param event Логируемое событие.
   */
  @Override
  protected void append(ILoggingEvent event) {
    // Применяем фильтр
    if (filter.decide(event) == FilterReply.ACCEPT) {
      String message = event.getFormattedMessage();
      // Здесь вы можете вызвать метод GUI для отображения журнала в интерфейсе
      ui.addLog(message);
    }
  }

  /** Класс фильтра, определяющего клиентские логи. */
  class LoggerFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
      // Проверяем, что лог относится к классу Client.
      if (event.getLoggerName().equals("io.deeplay.grandmastery.Client")) {
        return FilterReply.ACCEPT;
      }
      return FilterReply.DENY;
    }
  }
}
