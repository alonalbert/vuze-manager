package com.alon.vuze.vuzemanager.testing;

import com.alon.vuze.vuzemanager.Rule;
import com.alon.vuze.vuzemanager.RuleDialog;
import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.DebugLogger;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.DebugMessages;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;

@SuppressWarnings("ALL")
class Main {
  interface Factory {
    RuleDialog createRunDialog(Display display, RuleDialog.OnOkListener onOkListener);
  }

  public static void main(String[] args) throws IOException {
    final Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        final DebugLogger logger = new DebugLogger();
        bind(Logger.class).toInstance(logger);
        try {
          bind(Messages.class).toInstance(new DebugMessages());
        } catch (IOException e) {
          e.printStackTrace();
        }
        bind(Config.class).toInstance(new Config("out", logger));
        try {
          bind(PlexClient.class).toInstance(new PlexClient("10.0.0.6", 32400));
        } catch (ParserConfigurationException e) {
          e.printStackTrace();
        }

        install(new FactoryModuleBuilder().build(Factory.class));

        bind(TorrentAttribute.class).annotatedWith(Names.named(TA_COMPLETED_TIME))
            .toInstance(new MockTorrentAttribute());
        bind(TorrentAttribute.class).annotatedWith(Names.named(TorrentAttribute.TA_CATEGORY))
            .toInstance(new MockTorrentAttribute());
      }
    });
    final Display display = new Display();
    final RuleDialog ruleDialog = injector.getInstance(Factory.class)
        .createRunDialog(display, rule -> System.out.println(rule));
    ruleDialog.initializeAndOpen(new Rule("kkk", Rule.Action.CATEGORY_AUTO_DELETE, "1"));

    while (!ruleDialog.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }

  }


}