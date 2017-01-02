package com.alon.vuze.vuzemanager.logger;

import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
import org.gudy.azureus2.plugins.ui.components.UITextArea;

import javax.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;

@Singleton
public class VuzeLogger implements Logger , LoggerChannelListener {
  private final LoggerChannel loggerChannel;
  private final UITextArea logArea;

  public VuzeLogger(LoggerChannel loggerChannel, UITextArea logArea) {
    this.loggerChannel = loggerChannel;
    this.logArea = logArea;
    loggerChannel.addListener(this);
  }

  @Override
  public void log(String format, Object... args) {
    loggerChannel.log(String.format(format, args));
  }

  @Override
  public void log(Throwable e, String format, Object... args) {
    loggerChannel.log(String.format(format, args), e);
  }

  @Override
  public void messageLogged(int type, String content) {
    logArea.appendText(content + "\n");
  }

  @Override
  public void messageLogged(String str, Throwable error) {
    logArea.appendText(str + "\n");
    final StringWriter writer = new StringWriter();
    error.printStackTrace(new PrintWriter(writer));
    logArea.appendText(writer.toString() + "\n");
  }
}
