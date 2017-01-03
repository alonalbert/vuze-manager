package com.alon.vuze.vuzemanager.categories;

import com.alon.vuze.vuzemanager.categories.CategoryDialog.OnOkListener;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;

public class CategoriesModule extends AbstractModule {
  static final String TA_COMPLETED_TIME = "completedTime";

  private final TorrentManager torrentManager;

  public CategoriesModule(
      TorrentManager torrentManager) {

    this.torrentManager = torrentManager;
  }

  public interface Factory {
    CategoriesView create(Composite parent);
    CategoryDialog create(Display display, OnOkListener onOkListener);
    CategoryDialog create(Display display, OnOkListener onOkListener, CategoryConfig categoryConfig);
 }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
     .build(Factory.class));
    bind(CategoryAutoDeleter.class).asEagerSingleton();
    bind(TorrentAttribute.class).annotatedWith(Names.named(TA_COMPLETED_TIME))
        .toInstance(torrentManager.getPluginAttribute(TA_COMPLETED_TIME));
  }
}