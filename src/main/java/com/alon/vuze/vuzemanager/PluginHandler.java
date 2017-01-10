package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.Rules;
import com.alon.vuze.vuzemanager.ui.ProperSection;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.download.DownloadManagerListener;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;
import static com.alon.vuze.vuzemanager.rules.Rule.Action.FORCE_SEED;

@Singleton
class PluginHandler implements DownloadCompletionListener, DownloadListener, DownloadManagerListener {
  private static final String AUTO_DEST_CATEGORY = "auto-dest";

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
  private Config config;

  @Inject
  private DownloadManager downloadManager;

  @Inject
  public PluginHandler() {
  }

  @Override
  public void onCompletion(Download download) {
    setCompletedTime(download);
    handleForceSeed(download);
  }

  @Override
  public void stateChanged(Download download, int old_state, int new_state) {
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

  @Override
  public void downloadAdded(Download download) {
    handleBadReplaceWithProper(download);
  }

  @Override
  public void downloadRemoved(Download download) {

  }

  private void handleBadReplaceWithProper(Download properDownload) {
    final String directory = config.get(ProperSection.PROPER_DIRECTORY, "");
    final String category = config.get(ProperSection.PROPER_CATEGORY, "");
    final TvEpisode proper = TvEpisode.create(properDownload);
    if (proper == null || !proper.isProper()) {
      return;
    }
    logger.log("Proper download detected: %s", properDownload.getName());
    for (Download download : downloadManager.getDownloads()) {
      if (download == properDownload) {
        continue;
      }

      final TvEpisode episode = TvEpisode.create(download);
      if (!proper.isSameEpisode(episode)) {
        continue;
      }
      logger.log("Found matching episode: %s", download.getName());
      if (!category.isEmpty()) {
        download.setAttribute(categoryAttribute, category);
        logger.log("Set category of %s to %s", download.getName(), category);
      }
      if (!directory.isEmpty()) {
        try {
          logger.log("Moving %s from %s to %s", download.getName(), download.getSavePath(), directory);
          download.moveDataFiles(new File(directory));
        } catch (DownloadException e) {
          logger.log(e, "Failed to move %s", download.getName());
        }
      }
    }
  }

  private void handleForceSeed(Download download) {
    final String category = download.getAttribute(categoryAttribute);
    if (category == null) {
      return;
    }
    final Rule rule = rules.findFirst(FORCE_SEED, category);
    if (rule != null) {
      logger.log("Download %s force started.", download.getName());
      download.setForceStart(true);
    }
  }

  private void setCompletedTime(Download download) {
    if (download.getLongAttribute(completedTimeAttribute) == 0) {
      download.setLongAttribute(completedTimeAttribute, System.currentTimeMillis());
    }
  }
}
