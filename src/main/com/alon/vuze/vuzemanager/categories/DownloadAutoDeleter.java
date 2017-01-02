package com.alon.vuze.vuzemanager.categories;

import com.alon.vuze.vuzemanager.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.utils.TimeUtils;
import com.google.inject.Inject;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DownloadAutoDeleter {
  static final String TA_COMPLETED_TIME = "completedTime";

  private final DownloadManager downloadManager;

  @Inject
  Logger logger;

  @Inject
  Config config;

  private final TorrentAttribute categoryAttribute;
  private final TorrentAttribute completedTimeAttribute;

  @Inject
  public DownloadAutoDeleter(PluginInterface pluginInterface) {
    downloadManager = pluginInterface.getDownloadManager();
    final TorrentManager torrentManager = pluginInterface.getTorrentManager();
    categoryAttribute = torrentManager.getAttribute(TorrentAttribute.TA_CATEGORY);
    completedTimeAttribute = torrentManager.getPluginAttribute(TA_COMPLETED_TIME);

  }

  public void autoDeleteDownloads() {
    try {
      logger.log("Checking downloads...");
      final List<CategoryConfig> categories = config.getCategories().stream()
          .filter(category -> category.getAction() == CategoryConfig.Action.AUTO_DELETE)
          .collect(Collectors.toList());

      Arrays.stream(downloadManager.getDownloads())
          .filter(Download::isComplete)
          .forEach(download -> checkDownload(download, categories, System.currentTimeMillis()));
      logger.log("Done!!!");
    } catch (Exception e) {
      logger.log(e, "Unexpected error while checking downloads");
    }

  }

  private void checkDownload(Download download, List<CategoryConfig> categories, long now) {
    final long completedTime = download.getLongAttribute(completedTimeAttribute);
    if (completedTime == 0) {
      download.setLongAttribute(completedTimeAttribute, now);
      return;
    }
    final String category = download.getAttribute(categoryAttribute);
    for (CategoryConfig categoryConfig : categories) {
      if (categoryConfig.getWildcard().matches(category)) {
        final long duration = now - completedTime;
        final String durationString = TimeUtils.formatDuration(duration);
        logger.log(String.format("%s age is %s", download.getName(), durationString));
        if (TimeUnit.MILLISECONDS.toDays(duration) > categoryConfig.getDays()) {
          logger.log(String.format("Deleting %s after %s", durationString, download.getName()));
          try {
            download.remove(true, true);
          } catch (DownloadException | DownloadRemovalVetoException e) {
            logger.log(e, "Error deleting %s", download.getName());
          }
        }
      }
    }
  }

}
