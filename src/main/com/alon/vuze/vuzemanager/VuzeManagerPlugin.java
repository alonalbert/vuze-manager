package com.alon.vuze.vuzemanager;

import static com.alon.vuze.vuzemanager.MainView.VIEW_ID;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

public class VuzeManagerPlugin implements Plugin, UIManagerListener {


  private Injector injector;

  public void initialize(PluginInterface pluginInterface) throws PluginException {

    injector = Guice.createInjector(new VuzeManagerModule(pluginInterface));

    pluginInterface.getUIManager().addUIListener(this);
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
}
