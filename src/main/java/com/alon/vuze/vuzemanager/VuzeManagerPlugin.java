package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import com.alon.vuze.vuzemanager.ui.ConfigView;
import com.alon.vuze.vuzemanager.ui.PlexSection;
import com.alon.vuze.vuzemanager.ui.RuleDialog;
import com.alon.vuze.vuzemanager.ui.RulesSection;
import com.alon.vuze.vuzemanager.utils.NetworkUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;

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

    final DownloadEventNotifier eventNotifier = downloadManager.getGlobalDownloadEventNotifier();
    eventNotifier.addCompletionListener(pluginHandler);
    eventNotifier.addListener(pluginHandler);

    scheduler.scheduleAtFixedRate(() -> {
      categoryAutoDeleter.autoDeleteDownloads();
      plexAutoDeleter.autoDeleteDownloads();
    }, 0, 1, TimeUnit.DAYS);
  }


  public interface Factory {
    ConfigView createConfigView(Composite parent);

    RulesSection createSectionView(Composite parent);

    PlexSection createPlexSection(Composite parent);

    RuleDialog createRunDialog(Display display, RuleDialog.OnOkListener onOkListener);
  }

  @Override
  protected void configure() {
    bind(DownloadManager.class).toInstance(downloadManager);
    bind(Config.class).toInstance(config);
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);
    install(new FactoryModuleBuilder()
        .build(Factory.class));
    for (TorrentAttribute torrentAttribute : torrentManager.getDefinedAttributes()) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(torrentAttribute.getName()))
          .toInstance(torrentAttribute);
    }
    bind(TorrentAttribute.class).annotatedWith(Names.named(TA_COMPLETED_TIME))
        .toInstance(torrentManager.getPluginAttribute(TA_COMPLETED_TIME));
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
