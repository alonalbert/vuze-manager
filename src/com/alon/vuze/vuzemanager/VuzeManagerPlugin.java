package com.alon.vuze.vuzemanager;

import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManager;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

import static com.alon.vuze.vuzemanager.MainView.VIEWID;

public class VuzeManagerPlugin implements Plugin {

  public void initialize(PluginInterface pluginInterface) throws PluginException {
    final Config config = new Config(pluginInterface.getPluginDirectoryName(), new DebugLogger());

    final UIManager uiManager = pluginInterface.getUIManager();
    uiManager.addUIListener(
        new UIManagerListener() {
          @Override
          public void UIAttached(UIInstance instance) {
            if (instance instanceof UISWTInstance) {
              final UISWTInstance swtInstance = ((UISWTInstance) instance);

              final MainView view = new MainView(pluginInterface, config);
              swtInstance.addView(UISWTInstance.VIEW_MAIN, VIEWID, view);
              swtInstance.openMainView(VIEWID, view, null);
            }
          }

          @Override
          public void UIDetached(UIInstance instance) {
          }
        });
  }
}
