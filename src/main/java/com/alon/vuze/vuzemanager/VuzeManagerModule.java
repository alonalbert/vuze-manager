package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.Annotations.PluginDirectory;
import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;

import javax.xml.parsers.ParserConfigurationException;

class VuzeManagerModule extends AbstractModule {

  static final String TA_COMPLETED_TIME = "completedTime";
  private final VuzeMessages messages;
  private final VuzeLogger logger;
  private final PluginInterface pluginInterface;
  private final DownloadManager downloadManager;
  private final TorrentManager torrentManager;

  interface Factory {
    RuleDialog create(Display display, RuleDialog.OnOkListener onOkListener);
  }

  VuzeManagerModule(PluginInterface pluginInterface) {
    this.pluginInterface = pluginInterface;
    downloadManager = pluginInterface.getDownloadManager();
    torrentManager = pluginInterface.getTorrentManager();
    messages = new VuzeMessages();
    final String title = messages.getString("Views.plugins.VuzeManagerView.title");
    final BasicPluginViewModel viewModel = pluginInterface.getUIManager().createBasicPluginViewModel(title);
    logger = new VuzeLogger(pluginInterface.getLogger().getTimeStampedChannel(title), viewModel);
  }

  @Override
  protected void configure() {
    bind(PluginInterface.class).toInstance(pluginInterface);
    bind(DownloadManager.class).toInstance(downloadManager);
    bind(Config.class);
    bind(String.class).annotatedWith(PluginDirectory.class)
        .toInstance(pluginInterface.getPluginDirectoryName());
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);
    bind(ImageRepository.class);

    install(new FactoryModuleBuilder()
        .build(VuzeManagerModule.Factory.class));

    for (TorrentAttribute torrentAttribute : torrentManager.getDefinedAttributes()) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(torrentAttribute.getName()))
          .toInstance(torrentAttribute);
    }
    bind(CategoryAutoDeleter.class).asEagerSingleton();
    bind(TorrentAttribute.class).annotatedWith(Names.named(VuzeManagerModule.TA_COMPLETED_TIME))
        .toInstance(torrentManager.getPluginAttribute(VuzeManagerModule.TA_COMPLETED_TIME));

    try {
      bind(PlexClient.class).toInstance(new PlexClient("10.0.0.6", 32400));
    } catch (ParserConfigurationException e) {
      logger.log(e, "Failed to bind PlexClient");
    }
  }
}
