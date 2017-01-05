package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.Rules;
import com.alon.vuze.vuzemanager.utils.TimeUtils;
import com.alon.vuze.vuzemanager.utils.TorrentDeleter;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;
import static com.alon.vuze.vuzemanager.rules.Rule.Action.CATEGORY_AUTO_DELETE;

@Singleton
class CategoryAutoDeleter {

  @Inject
  private DownloadManager downloadManager;

  @Inject
  private Logger logger;

  @Inject
  private TorrentDeleter torrentDeleter;

  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  private TorrentAttribute categoryAttribute;

  @Inject
  @Named(TA_COMPLETED_TIME)
  private TorrentAttribute completedTimeAttribute;

  @Inject
  private Rules rules;

  @Inject
  public CategoryAutoDeleter() {
  }

  void autoDeleteDownloads() {
    try {
      logger.log("Checking downloads...");
      Arrays.stream(downloadManager.getDownloads())
          .filter(Download::isComplete)
          .forEach(download -> checkDownload(download, System.currentTimeMillis()));
      logger.log("Done!!!");
    } catch (Exception e) {
      logger.log(e, "Unexpected error while checking downloads");
    }
  }

  private void checkDownload(Download download, long now) {
    final long completedTime = download.getLongAttribute(completedTimeAttribute);
    if (completedTime == 0) {
      download.setLongAttribute(completedTimeAttribute, now);
      return;
    }
    final String category = download.getAttribute(categoryAttribute);
    final Rule rule = rules.findFirst(CATEGORY_AUTO_DELETE, category);
    if (rule != null) {
      final long duration = now - completedTime;
      final String durationString = TimeUtils.formatDuration(duration);
      logger.log(String.format("%s age is %s", download.getName(), durationString));
      if (TimeUnit.MILLISECONDS.toDays(duration) >= rule.getArgAsInt()) {
        logger.log(String.format("Deleting %s after %s", durationString, download.getName()));
        torrentDeleter.deleteDownload(download);
      }
    }
  }

}
