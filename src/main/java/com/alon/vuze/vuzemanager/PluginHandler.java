package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.logger.Logger;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;

@Singleton
class PluginHandler implements DownloadCompletionListener, UIManagerListener {
  private static final String VIEW_ID = "VuzeManagerView";

  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  private TorrentAttribute categoryAttribute;

  @Inject
  @Named(TA_COMPLETED_TIME)
  private TorrentAttribute completedTimeAttribute;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @Inject
  private Set<Rule> rules;

  @Inject
  private Logger logger;

  @Inject
  private RulesView rulesView;

  @Inject
  public PluginHandler() {
  }

  @Override
  public void onCompletion(Download download) {
    download.setLongAttribute(completedTimeAttribute, System.currentTimeMillis());

    final String category = download.getAttribute(categoryAttribute);
    if (category == null) {
      return;
    }
    rules.stream()
        .filter(rule -> rule.getAction() == Rule.Action.FORCE_SEED && rule.getMatcher().matches(category))
        .forEach(rule -> forceStart(download));
  }

  private void forceStart(Download download) {
    logger.log("Download %s force started.", download);
    download.setForceStart(true);
  }

  @Override
  public void UIAttached(UIInstance instance) {
    if (instance instanceof UISWTInstance) {
      final UISWTInstance swtInstance = ((UISWTInstance) instance);
      swtInstance.addView(UISWTInstance.VIEW_MAIN, VIEW_ID, rulesView);
      swtInstance.openMainView(VIEW_ID, rulesView, null);
    }
  }

  @Override
  public void UIDetached(UIInstance instance) {
  }
}
