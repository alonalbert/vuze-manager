package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.utils.TimeUtils;
import com.alon.vuze.vuzemanager.utils.TorrentDeleted;
import com.google.inject.Provider;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;
import static com.alon.vuze.vuzemanager.VuzeManagerPlugin.FAKE_DELETE;

@Singleton
class CategoryAutoDeleter {

  @Inject
  private DownloadManager downloadManager;

  @Inject
  private Logger logger;

  @Inject
  private Config config;

  @Inject
  private TorrentDeleted torrentDeleter;

  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  private TorrentAttribute categoryAttribute;

  @Inject
  @Named(TA_COMPLETED_TIME)
  private TorrentAttribute completedTimeAttribute;

  @Named(FAKE_DELETE)
  @Inject private
  Provider<Boolean> fakeDeleteProvider;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @Inject
  private Set<Rule> rules;

  @Inject
  public CategoryAutoDeleter() {
  }

  void autoDeleteDownloads() {
    try {
      logger.log("Checking downloads...");
      final List<Rule> relevantRules = rules.stream()
          .filter(category -> category.getAction() == Rule.Action.CATEGORY_AUTO_DELETE)
          .collect(Collectors.toList());

      logger.log("Found %d relevant rules", relevantRules.size());
      if (relevantRules.size() > 0) {
        Arrays.stream(downloadManager.getDownloads())
            .filter(Download::isComplete)
            .forEach(download -> checkDownload(download, relevantRules, System.currentTimeMillis()));
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
        if (TimeUnit.MILLISECONDS.toDays(duration) >= rule.getArgAsInt()) {
          logger.log(String.format("Deleting %s after %s", durationString, download.getName()));
          torrentDeleter.deleteDownload(download);
        }
      }
    }
  }

}
