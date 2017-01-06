package com.alon.vuze.vuzemanager.testing;

import com.alon.vuze.vuzemanager.VuzeManagerPlugin;
import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.DebugLogger;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.DebugMessages;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.rules.Rules;
import com.alon.vuze.vuzemanager.ui.ConfigView;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;

@SuppressWarnings("ALL")
class Main {

  public static void main(String[] args) throws IOException {
    final DebugLogger logger = new DebugLogger();
    final Config config = new Config("out", logger);
    final Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Logger.class).toInstance(logger);
        bind(Rules.class).toInstance(new Rules(config));
        try {
          bind(Messages.class).toInstance(new DebugMessages());
        } catch (IOException e) {
          e.printStackTrace();
        }
        bind(Config.class).toInstance(config);
        try {
          bind(PlexClient.class).toInstance(new PlexClient("10.0.0.6", 32400));
        } catch (ParserConfigurationException e) {
          e.printStackTrace();
        }

        bind(DownloadManager.class).to(FakeDownloadManager.class);
        install(new FactoryModuleBuilder().build(VuzeManagerPlugin.Factory.class));

        bind(TorrentAttribute.class).annotatedWith(Names.named(TA_COMPLETED_TIME))
            .toInstance(new MockTorrentAttribute());
        bind(TorrentAttribute.class).annotatedWith(Names.named(TorrentAttribute.TA_CATEGORY))
            .toInstance(new MockTorrentAttribute());
      }
    });
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());
    shell.setSize(new Point(800, 600));

    final ConfigView view = injector.getInstance(VuzeManagerPlugin.Factory.class).createConfigView(shell);
    view.addDisposeListener(e -> view.save());
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }
}