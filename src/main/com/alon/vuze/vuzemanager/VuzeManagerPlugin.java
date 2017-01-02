package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.categories.DownloadAutoDeleter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

import static com.alon.vuze.vuzemanager.MainView.VIEW_ID;

public class VuzeManagerPlugin implements Plugin, UIManagerListener {


  private Injector injector;

  public void initialize(PluginInterface pluginInterface) throws PluginException {

    injector = Guice.createInjector(new VuzeManagerModule(pluginInterface));

    pluginInterface.getUIManager().addUIListener(this);

    createConfigModule(pluginInterface);
  }

  @Override
  public void UIAttached(UIInstance instance) {
    if (instance instanceof UISWTInstance) {
      final UISWTInstance swtInstance = ((UISWTInstance) instance);
      final MainView view =  injector.getInstance(MainView.class);
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

    final DownloadAutoDeleter deleter = injector.getInstance(DownloadAutoDeleter.class);
    configModel.addActionParameter2(null, "vuzeManager.categories.config.checkNow")
        .addListener(param -> deleter.autoDeleteDownloads());
  }
}
