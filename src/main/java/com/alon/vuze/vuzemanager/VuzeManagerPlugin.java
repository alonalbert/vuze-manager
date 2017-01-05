package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
import com.alon.vuze.vuzemanager.rules.RuleDialog;
import com.alon.vuze.vuzemanager.utils.NetworkUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
import org.gudy.azureus2.plugins.ui.config.StringParameter;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;

import javax.inject.Named;
import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_ALREADY_MOVED;
import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;

@SuppressWarnings("WeakerAccess")
public class VuzeManagerPlugin extends AbstractModule implements Plugin {
  public static final String VUZE_ROOT = "VuzeRoot";
  public static final String PLEX_ROOT = "PlexRoot";
  public static final String FAKE_DELETE = "FakeDelete";

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private DownloadManager downloadManager;
  private TorrentManager torrentManager;

  private VuzeMessages messages;
  private VuzeLogger logger;

  private StringParameter plexServer;
  private StringParameter plexPort;
  private StringParameter vuzeRoot;
  private StringParameter plexRoot;

  private CategoryAutoDeleter categoryAutoDeleter;
  private PlexAutoDeleter plexAutoDeleter;
  private Config config;
  private BooleanParameter fakeDelete;

  public void initialize(PluginInterface pluginInterface) throws PluginException {
    torrentManager = pluginInterface.getTorrentManager();
    downloadManager = pluginInterface.getDownloadManager();

    createConfigModule(pluginInterface);

    messages = new VuzeMessages();
    logger = new VuzeLogger(pluginInterface, messages);
    config = new Config(pluginInterface.getPerUserPluginDirectoryName(), logger);

    final Injector injector = Guice.createInjector(this);
    categoryAutoDeleter = injector.getInstance(CategoryAutoDeleter.class);
    plexAutoDeleter = injector.getInstance(PlexAutoDeleter.class);

    final PluginHandler pluginHandler = injector.getInstance(PluginHandler.class);
    pluginInterface.getUIManager().addUIListener(pluginHandler);

    final DownloadEventNotifier eventNotifier = downloadManager.getGlobalDownloadEventNotifier();
    eventNotifier.addCompletionListener(pluginHandler);
    eventNotifier.addListener(pluginHandler);

    scheduler.scheduleAtFixedRate(() -> {
      categoryAutoDeleter.autoDeleteDownloads();
      plexAutoDeleter.autoDeleteDownloads();
    }, 0, 1, TimeUnit.DAYS);
  }


  public interface Factory {
    RuleDialog createRunDialog(Display display, RuleDialog.OnOkListener onOkListener);
  }

  @Override
  protected void configure() {
    bind(DownloadManager.class).toInstance(downloadManager);

    bind(Config.class).toInstance(config);
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);
    bind(ImageRepository.class).asEagerSingleton();

    install(new FactoryModuleBuilder()
        .build(Factory.class));

    for (TorrentAttribute torrentAttribute : torrentManager.getDefinedAttributes()) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(torrentAttribute.getName()))
          .toInstance(torrentAttribute);
    }
    bind(TorrentAttribute.class).annotatedWith(Names.named(TA_COMPLETED_TIME))
        .toInstance(torrentManager.getPluginAttribute(TA_COMPLETED_TIME));
    bind(TorrentAttribute.class).annotatedWith(Names.named(TA_ALREADY_MOVED))
        .toInstance(torrentManager.getPluginAttribute(TA_ALREADY_MOVED));
  }

  @Provides
   PlexClient providePlexClient() {
    try {
      return new PlexClient(plexServer.getValue(), Integer.valueOf(plexPort.getValue()));
    } catch (ParserConfigurationException e) {
      logger.log(e, "Failed to bind PlexClient");
      return null;
    }
  }

  @Provides
  @Named(VUZE_ROOT)
  String provideVuzeRoot() {
    return vuzeRoot.getValue();
  }

  @Provides
  @Named(PLEX_ROOT)
  String providePlexRoot() {
    return plexRoot.getValue();
  }

  @Provides
  @Named(FAKE_DELETE)
  boolean provideFakeDelete() {
    return fakeDelete.getValue();
  }

  private void createConfigModule(PluginInterface pluginInterface) {
    final BasicPluginConfigModel configModel = pluginInterface.getUIManager()
        .createBasicPluginConfigModel("ConfigView.section.vuzeManager");

    configModel.addLabelParameter2("vuzeManager.config.title");

    plexServer = configModel.addStringParameter2("server", "vuzeManager.config.plexServer", NetworkUtils.getLocalhostAddress());
    plexPort = configModel.addStringParameter2("port", "vuzeManager.config.plexPort", "32400");
    vuzeRoot = configModel.addStringParameter2("vuze-root", "vuzeManager.config.vuzeRoot", "");
    plexRoot = configModel.addStringParameter2("plex-root", "vuzeManager.config.plexRoot", "");

    fakeDelete = configModel.addBooleanParameter2("fake-delete", "vuzeManager.config.fakeDelete", false);

    configModel.addActionParameter2(null, "vuzeManager.config.checkCategoryDeleteNow")
        .addListener(param -> scheduler.schedule(() ->
            categoryAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS));

    configModel.addActionParameter2(null, "vuzeManager.config.checkPlexDeleteNow")
        .addListener(param -> scheduler.schedule(() ->
            plexAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS));
  }

}
