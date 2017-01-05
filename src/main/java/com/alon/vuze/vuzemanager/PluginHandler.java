package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.Rules;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;
import static com.alon.vuze.vuzemanager.rules.Rule.Action.FORCE_SEED;

@Singleton
class PluginHandler implements UIManagerListener, DownloadCompletionListener, DownloadListener {
  private static final String VIEW_ID = "VuzeManagerView";
  public static final String AUTO_DEST_CATEGORY = "auto-dest";

  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  private TorrentAttribute categoryAttribute;

  @Inject
  @Named(TA_COMPLETED_TIME)
  private TorrentAttribute completedTimeAttribute;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @Inject
  private Rules rules;

  @Inject
  private Logger logger;

  @Inject
  private MainView mainView;

  @Inject
  public PluginHandler() {
  }

  @Override
  public void onCompletion(Download download) {
    if (download.getLongAttribute(completedTimeAttribute) == 0) {
      download.setLongAttribute(completedTimeAttribute, System.currentTimeMillis());
    }

    final String category = download.getAttribute(categoryAttribute);
    if (category == null) {
      return;
    }
    final Rule rule = rules.findFirst(FORCE_SEED, category);
    if (rule != null) {
      logger.log("Download %s force started.", download);
      download.setForceStart(true);
    }
  }

  @Override
  public void UIAttached(UIInstance instance) {
    if (instance instanceof UISWTInstance) {
      final UISWTInstance swtInstance = ((UISWTInstance) instance);
      swtInstance.addView(UISWTInstance.VIEW_MAIN, VIEW_ID, mainView);
      swtInstance.openMainView(VIEW_ID, mainView, null);
    }
  }

  @Override
  public void UIDetached(UIInstance instance) {
  }

  @Override
  public void stateChanged(Download download, int old_state, int new_state) {
    final String downloadName = download.getName();
    logger.log("Download %s: %s - > %s", downloadName, Download.ST_NAMES[old_state], Download.ST_NAMES[new_state]);

    maybeAutoMove(download);
  }

  private void maybeAutoMove(Download download) {
    final String downloadName = download.getName();
    if (downloadName.startsWith("Metadata download for ")) {
      return;
    }
    final String category = download.getAttribute(categoryAttribute);

    if (category != null && !category.isEmpty() ) {
      return;
    }
    final Rule rule = rules.findFirst(Rule.Action.AUTO_DESTINATION, downloadName);
    if (rule == null) {
      return;
    }
    final String destination = rule.getArg();
    logger.log("Moving %s to %s", downloadName, destination);
    if (!download.canMoveDataFiles()) {
      logger.log("Download data files can't be moved: %s", downloadName);
      return;
    }
    final File file = new File(destination);
    if (!file.isDirectory()) {
      if (file.exists()) {
        logger.log("%d exists as a file. Can't move download", destination);
        return;
      }
      file.mkdirs();
    }
    try {
      download.moveDataFiles(file);
      download.setAttribute(categoryAttribute, AUTO_DEST_CATEGORY);
    } catch (DownloadException e) {
      logger.log(e, "Could not move download");
    }
  }

  @Override
  public void positionChanged(Download download, int oldPosition, int newPosition) {

  }
}
