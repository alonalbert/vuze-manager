package com.alon.vuze.vuzemanager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VuzeManagerPlugin implements Plugin, UIManagerListener {
  private static final String VIEW_ID = "VuzeManagerView";

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private Injector injector;
  private CategoryAutoDeleter categoryAutoDeleter;
  private PlexAutoDeleter plexAutoDeleter;

  public void initialize(PluginInterface pluginInterface) throws PluginException {

    injector = Guice.createInjector(new VuzeManagerModule(pluginInterface));
    categoryAutoDeleter = injector.getInstance(CategoryAutoDeleter.class);
    plexAutoDeleter = injector.getInstance(PlexAutoDeleter.class);

    pluginInterface.getUIManager().addUIListener(this);

    createConfigModule(pluginInterface);

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

    configModel.addActionParameter2(null, "vuzeManager.config.checkCategoryDeleteNow")
        .addListener(param -> scheduler.schedule(() ->
            categoryAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS));

    configModel.addActionParameter2(null, "vuzeManager.config.checkPlexDeleteNow")
        .addListener(param -> scheduler.schedule(() ->
            plexAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS));
  }
}
