package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import com.alon.vuze.vuzemanager.ui.PlexSection;
import com.alon.vuze.vuzemanager.utils.NetworkUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;

import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("WeakerAccess")
public class VuzeManagerPlugin extends AbstractModule implements Plugin {
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private DownloadManager downloadManager;
  private TorrentManager torrentManager;

  private VuzeMessages messages;
  private VuzeLogger logger;

  private CategoryAutoDeleter categoryAutoDeleter;
  private PlexAutoDeleter plexAutoDeleter;
  private Config config;

  public void initialize(PluginInterface pluginInterface) throws PluginException {
    torrentManager = pluginInterface.getTorrentManager();
    downloadManager = pluginInterface.getDownloadManager();

    messages = new VuzeMessages();
    logger = new VuzeLogger(pluginInterface, messages);
    config = new Config(pluginInterface.getPerUserPluginDirectoryName(), logger);

    final Injector injector = Guice.createInjector(this);
    categoryAutoDeleter = injector.getInstance(CategoryAutoDeleter.class);
    plexAutoDeleter = injector.getInstance(PlexAutoDeleter.class);

    final PluginHandler pluginHandler = injector.getInstance(PluginHandler.class);

    //noinspection deprecation
    pluginInterface.addConfigSection(injector.getInstance(VuzeManagerConfigSection.class));

    downloadManager.addListener(pluginHandler);
    final DownloadEventNotifier eventNotifier = downloadManager.getGlobalDownloadEventNotifier();
    eventNotifier.addCompletionListener(pluginHandler);
    eventNotifier.addListener(pluginHandler);

    scheduler.scheduleAtFixedRate(() -> {
      categoryAutoDeleter.autoDeleteDownloads();
      plexAutoDeleter.autoDeleteDownloads();
    }, 0, 4, TimeUnit.HOURS);
  }

  @Override
  protected void configure() {
    bind(DownloadManager.class).toInstance(downloadManager);
    bind(TorrentManager.class).toInstance(torrentManager);
    bind(Config.class).toInstance(config);
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);
    install(new FactoryModuleBuilder()
        .build(ViewFactory.class));
    for (TorrentAttribute torrentAttribute : torrentManager.getDefinedAttributes()) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(torrentAttribute.getName()))
          .toInstance(torrentAttribute);
    }
    for (String attribute : PluginTorrentAttributes.attributes) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(attribute))
          .toInstance(torrentManager.getPluginAttribute(attribute));
    }
  }

  @Provides
  PlexClient providePlexClient() {
    try {
      return new PlexClient(
          config.get(PlexSection.PLEX_HOST, NetworkUtils.getLocalhostAddress()),
          config.get(PlexSection.PLEX_PORT, 32400));
    } catch (ParserConfigurationException e) {
      logger.log(e, "Failed to bind PlexClient");
      return null;
    }
  }
}
