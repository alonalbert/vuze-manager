package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.logger.VuzeLogger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.resources.VuzeMessages;
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
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.plugins.ui.config.StringParameter;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

import javax.inject.Named;
import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class VuzeManagerPlugin extends AbstractModule implements Plugin, UIManagerListener {
  private static final String VIEW_ID = "VuzeManagerView";
  static final String TA_COMPLETED_TIME = "completedTime";

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


  private PluginInterface pluginInterface;
  private DownloadManager downloadManager;
  private TorrentManager torrentManager;

  private VuzeMessages messages;
  private VuzeLogger logger;

  private StringParameter plexServer;
  private StringParameter plexPort;
  private StringParameter vuzeRoot;
  private StringParameter plexRoot;

  private Injector injector;
  private CategoryAutoDeleter categoryAutoDeleter;
  private PlexAutoDeleter plexAutoDeleter;

  public void initialize(PluginInterface pluginInterface) throws PluginException {
    this.pluginInterface = pluginInterface;
    torrentManager = pluginInterface.getTorrentManager();
    downloadManager = pluginInterface.getDownloadManager();

    messages = new VuzeMessages();

    createConfigModule(pluginInterface);

    final String title = messages.getString("Views.plugins.VuzeManagerView.title");
    final BasicPluginViewModel viewModel = pluginInterface.getUIManager().createBasicPluginViewModel(title);
    logger = new VuzeLogger(pluginInterface.getLogger().getTimeStampedChannel(title), viewModel);

    injector = Guice.createInjector(this);
    categoryAutoDeleter = injector.getInstance(CategoryAutoDeleter.class);
    plexAutoDeleter = injector.getInstance(PlexAutoDeleter.class);

    pluginInterface.getUIManager().addUIListener(this);

    scheduler.scheduleAtFixedRate(() -> {
      categoryAutoDeleter.autoDeleteDownloads();
      plexAutoDeleter.autoDeleteDownloads();
    }, 0, 1, TimeUnit.DAYS);
  }

  @Override
  public void UIAttached(UIInstance instance) {
    if (instance instanceof UISWTInstance) {
      final UISWTInstance swtInstance = ((UISWTInstance) instance);
      final UISWTViewEventListener view =  injector.getInstance(RulesView.class);
      swtInstance.addView(UISWTInstance.VIEW_MAIN, VIEW_ID, view);
      swtInstance.openMainView(VIEW_ID, view, null);
    }
  }

  @Override
  public void UIDetached(UIInstance instance) {

  }

  private void createConfigModule(PluginInterface pluginInterface) {
    final BasicPluginConfigModel configModel = pluginInterface.getUIManager()
        .createBasicPluginConfigModel("ConfigView.section.vuzeManager");

    configModel.addLabelParameter2("vuzeManager.config.title");

    plexServer = configModel.addStringParameter2("server", "vuzeManager.config.plexServer", "localhost");
    plexPort = configModel.addStringParameter2("port", "vuzeManager.config.plexPort", "32400");
    vuzeRoot = configModel.addStringParameter2("vuze-root", "vuzeManager.config.vuzeRoot", "");
    plexRoot = configModel.addStringParameter2("plex-root", "vuzeManager.config.plexRoot", "");

    configModel.addActionParameter2(null, "vuzeManager.config.checkCategoryDeleteNow")
        .addListener(param -> scheduler.schedule(() ->
            categoryAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS));

    configModel.addActionParameter2(null, "vuzeManager.config.checkPlexDeleteNow")
        .addListener(param -> scheduler.schedule(() ->
            plexAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS));
  }

  interface Factory {
    RuleDialog createRunDialog(Display display, RuleDialog.OnOkListener onOkListener);
  }

  @Override
  protected void configure() {
    bind(PluginInterface.class).toInstance(pluginInterface);
    bind(DownloadManager.class).toInstance(downloadManager);
    bind(Config.class);
    bind(String.class).annotatedWith(Names.named("PluginDirectory"))
        .toInstance(pluginInterface.getPluginDirectoryName());
    bind(Logger.class).toInstance(logger);
    bind(Messages.class).toInstance(messages);
    bind(ImageRepository.class);

    install(new FactoryModuleBuilder()
        .build(Factory.class));

    for (TorrentAttribute torrentAttribute : torrentManager.getDefinedAttributes()) {
      bind(TorrentAttribute.class).annotatedWith(Names.named(torrentAttribute.getName()))
          .toInstance(torrentAttribute);
    }
    bind(CategoryAutoDeleter.class).asEagerSingleton();
    bind(TorrentAttribute.class).annotatedWith(Names.named(TA_COMPLETED_TIME))
        .toInstance(torrentManager.getPluginAttribute(TA_COMPLETED_TIME));
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
  @Named("VuzeRoot")
  String provideVuzeRoot() {
    return vuzeRoot.getValue();
  }

  @Provides
  @Named("PlexRoot")
  String providePlexRoot() {
    return plexRoot.getValue();
  }
}
