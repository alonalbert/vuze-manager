package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.utils.TimeUtils;
import com.google.inject.Inject;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class CategoryAutoDeleter {

  @SuppressWarnings("unused")
  @Inject
  private DownloadManager downloadManager;

  @SuppressWarnings("unused")
  @Inject
  private Logger logger;

  @SuppressWarnings("unused")
  @Inject
  private Config config;

  @SuppressWarnings("unused")
  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  private TorrentAttribute categoryAttribute;

  @SuppressWarnings("unused")
  @Inject
  @Named(VuzeManagerPlugin.TA_COMPLETED_TIME)
  private TorrentAttribute completedTimeAttribute;

  @Inject
  public CategoryAutoDeleter() {
  }

  void autoDeleteDownloads() {
    try {
      logger.log("Checking downloads...");
      final List<Rule> rules = RulesView.getRulesFromConfig(config).stream()
          .filter(category -> category.getAction() == Rule.Action.CATEGORY_AUTO_DELETE)
          .collect(Collectors.toList());

      logger.log("Found %d relevant rules", rules.size());
      if (rules.size() > 0) {
        Arrays.stream(downloadManager.getDownloads())
            .filter(Download::isComplete)
            .forEach(download -> checkDownload(download, rules, System.currentTimeMillis()));
      }
      logger.log("Done!!!");
    } catch (Exception e) {
      logger.log(e, "Unexpected error while checking downloads");
    }
  }

  private void checkDownload(Download download, List<Rule> categories, long now) {
    final long completedTime = download.getLongAttribute(completedTimeAttribute);
    if (completedTime == 0) {
      download.setLongAttribute(completedTimeAttribute, now);
      return;
    }
    final String category = download.getAttribute(categoryAttribute);
    for (Rule rule : categories) {
      if (rule.getMatcher().matches(category)) {
        final long duration = now - completedTime;
        final String durationString = TimeUtils.formatDuration(duration);
        logger.log(String.format("%s age is %s", download.getName(), durationString));
        if (TimeUnit.MILLISECONDS.toDays(duration) > rule.getArgAsInt()) {
          logger.log(String.format("Deleting %s after %s", durationString, download.getName()));
//          try {
//            download.remove(true, true);
//          } catch (DownloadException | DownloadRemovalVetoException e) {
//            logger.log(e, "Error deleting %s", download.getName());
//          }
        }
      }
    }
  }

}
