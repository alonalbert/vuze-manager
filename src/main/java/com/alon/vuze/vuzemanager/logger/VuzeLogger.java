package com.alon.vuze.vuzemanager.logger;

import com.alon.vuze.vuzemanager.resources.Messages;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
import org.gudy.azureus2.plugins.ui.components.UITextArea;
import org.gudy.azureus2.plugins.ui.components.UITextField;
import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;

import java.io.PrintWriter;
import java.io.StringWriter;

public class VuzeLogger implements Logger , LoggerChannelListener {
  private final LoggerChannel loggerChannel;
  private final UITextArea logArea;
  private final UITextField activity;

  public VuzeLogger(PluginInterface pluginInterface, Messages messages) {
    final String title = messages.getString("Views.plugins.VuzeManagerView.title");
    final BasicPluginViewModel viewModel = pluginInterface.getUIManager().createBasicPluginViewModel(title);
    loggerChannel = pluginInterface.getLogger().getTimeStampedChannel(title);
    logArea = viewModel.getLogArea();
    activity = viewModel.getActivity();
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
  public void setStatus(String status) {
    activity.setText(status);
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
