package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.Annotations.PluginDirectory;
import com.alon.vuze.vuzemanager.categories.CategoriesModule;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import com.google.inject.AbstractModule;
import org.gudy.azureus2.plugins.PluginInterface;

class VuzeManagerModule extends AbstractModule {

  private final VuzeMessages messages;
  private final VuzeLogger logger;
  private final PluginInterface pluginInterface;

  VuzeManagerModule(PluginInterface pluginInterface) {
    this.pluginInterface = pluginInterface;
    messages = new VuzeMessages();
    final String title = messages.getString("Views.plugins.VuzeManagerView.title");
    logger = new VuzeLogger(
        pluginInterface.getLogger().getTimeStampedChannel(title),
        pluginInterface.getUIManager().createBasicPluginViewModel(title).getLogArea());
  }

  @Override
  protected void configure() {
    install(new CategoriesModule());
    bind(PluginInterface.class).toInstance(pluginInterface);
    bind(String.class).annotatedWith(PluginDirectory.class).toInstance(pluginInterface.getPluginDirectoryName());
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);
    bind(Config.class);
  }
}
