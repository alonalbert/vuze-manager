package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.Rules;
import com.alon.vuze.vuzemanager.ui.ProperSection;
import com.alon.vuze.vuzemanager.utils.PathUtils;
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
import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_SORTED;
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

  @Inject
  @Named(TA_SORTED)
  private TorrentAttribute sortedAttribute;

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
    if (download.getName().startsWith("Metadata download for ")) {
      return;
    }
    handleAutoDestination(download);
    handleSortTvShows(download);

  }

  private void handleSortTvShows(Download download) {
    // Don't handle episodes replaced by PROPER
    final String category = config.get(ProperSection.PROPER_CATEGORY, "");
    if (!category.isEmpty()) {
      if (category.equals(download.getAttribute(categoryAttribute))) {
        return;
      }
    }

    final TvEpisode episode = TvEpisode.create(download);
    if (episode == null) {
      return;
    }
    final String savePath = download.getSavePath();
    final String root = PathUtils.getSaveRoot(savePath);
    final String sorted = download.getAttribute(sortedAttribute);
    if (root.equals(sorted)) {
      return;
    }
    logger.log("Original save path: %s", savePath);

    final String destination = String.format("%s/%s/S%02d", root, episode.getName(), episode.getSeason());
    logger.log("Sorting %s into %s", download.getName(), destination);
    final boolean moved = moveDownload(download, destination);
    if (moved) {
      download.setAttribute(sortedAttribute, destination);
    }
  }

  private void handleAutoDestination(Download download) {
    String downloadName = download.getName();
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
    final boolean moved = moveDownload(download, destination);
    if (moved) {
      download.setAttribute(categoryAttribute, AUTO_DEST_CATEGORY);
    }
  }

  private boolean moveDownload(Download download, String destination) {
    final String savePath = new File(download.getSavePath()).getParent();
    final String name = download.getName();
    if (savePath.equals(destination)) {
      logger.log("%s is already in %s. Not moving it", name, destination);
      return false;
    }
    if (!download.canMoveDataFiles()) {
      logger.log("Download data files can't be moved: %s", name);
      return false;
    }
    final File file = new File(destination);
    if (!file.isDirectory()) {
      if (file.exists()) {
        logger.log("%d exists as a file. Can't move download", destination);
        return false;
      }
      file.mkdirs();
    }
    try {
      download.moveDataFiles(file);
      return true;
    } catch (DownloadException e) {
      logger.log(e, "Could not move download");
    }
    return false;
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
