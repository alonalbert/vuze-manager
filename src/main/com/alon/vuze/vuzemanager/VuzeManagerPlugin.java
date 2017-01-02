package com.alon.vuze.vuzemanager;

import static com.alon.vuze.vuzemanager.MainView.VIEW_ID;

import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManager;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.plugins.ui.components.UITextArea;
import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

public class VuzeManagerPlugin implements Plugin, UIManagerListener {

  private Config config;
  private PluginInterface pluginInterface;
  private VuzeLogger logger;
  private Messages messages;

  public void initialize(PluginInterface pluginInterface) throws PluginException {
    this.pluginInterface = pluginInterface;

    messages = new VuzeMessages();
    final String title = messages.getString("Views.plugins.VuzeManagerView.title");
    final LoggerChannel loggerChannel = pluginInterface.getLogger().getTimeStampedChannel(title);
    final BasicPluginViewModel viewModel = pluginInterface.getUIManager().createBasicPluginViewModel(title);
    final UITextArea logArea = viewModel.getLogArea();
    this.logger = new VuzeLogger(loggerChannel, logArea);

    config = new Config(pluginInterface.getPluginDirectoryName(), logger);

    final UIManager uiManager = this.pluginInterface.getUIManager();
    uiManager.addUIListener(this);
  }

  @Override
  public void UIAttached(UIInstance instance) {
    if (instance instanceof UISWTInstance) {
      final UISWTInstance swtInstance = ((UISWTInstance) instance);
      final MainView view = new MainView(pluginInterface, config, logger, messages);
      swtInstance.addView(UISWTInstance.VIEW_MAIN, VIEW_ID, view);
      swtInstance.openMainView(VIEW_ID, view, null);
    }
  }

  @Override
  public void UIDetached(UIInstance instance) {

  }
}
