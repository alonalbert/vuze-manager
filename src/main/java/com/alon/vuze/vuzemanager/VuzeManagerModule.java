package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.Annotations.PluginDirectory;
import com.alon.vuze.vuzemanager.categories.CategoriesModule;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;

class VuzeManagerModule extends AbstractModule {

  private final VuzeMessages messages;
  private final VuzeLogger logger;
  private final PluginInterface pluginInterface;
  private final DownloadManager downloadManager;
  private final TorrentManager torrentManager;

  VuzeManagerModule(PluginInterface pluginInterface) {
    this.pluginInterface = pluginInterface;
    downloadManager = pluginInterface.getDownloadManager();
    torrentManager = pluginInterface.getTorrentManager();
    messages = new VuzeMessages();
    final String title = messages.getString("Views.plugins.VuzeManagerView.title");
    logger = new VuzeLogger(
        pluginInterface.getLogger().getTimeStampedChannel(title),
        pluginInterface.getUIManager().createBasicPluginViewModel(title).getLogArea());
  }

  @Override
  protected void configure() {
    install(new CategoriesModule(torrentManager));
    bind(PluginInterface.class).toInstance(pluginInterface);
    bind(DownloadManager.class).toInstance(downloadManager);
    bind(Config.class);
    bind(String.class).annotatedWith(PluginDirectory.class)
        .toInstance(pluginInterface.getPluginDirectoryName());
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);

    for (TorrentAttribute torrentAttribute : torrentManager.getDefinedAttributes()) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(torrentAttribute.getName()))
          .toInstance(torrentAttribute);
    }
  }
}
