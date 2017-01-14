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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alon.vuze.vuzemanager.PluginTorrentAttributes.TA_COMPLETED_TIME;
import static com.alon.vuze.vuzemanager.rules.Rule.Action.CATEGORY_AUTO_DELETE;

@Singleton
public class CategoryAutoDeleter {

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

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @Inject
  private Rules rules;

  @Inject
  public CategoryAutoDeleter() {
  }

  public void autoDeleteDownloads() {
    final List<Rule> matchingRules = rules.stream()
        .filter(rule -> rule.getAction() == CATEGORY_AUTO_DELETE)
        .collect(Collectors.toList());
    if (matchingRules.size() == 0) {
      return;
    }
    try {
      DeletionStats stats = new DeletionStats(30);
      logger.log("");
      logger.log("============== Category Auto Delete Begin ==============");
      logger.log("Checking downloads...");
      Arrays.stream(downloadManager.getDownloads())
          .filter(Download::isComplete)
          .forEach(download -> checkDownload(download, matchingRules, System.currentTimeMillis(), stats));
      for (int i = 0; i < 30; i++) {
        if (stats.getNum(i) > 0) {
          logger.log(String.format("%d GB in %d downloads will be deleted in %d day",
              stats.getNumGb(i), stats.getNum(i), i));
        }
      }
      logger.log("============== Category Auto Delete End ==============");
      logger.log("");
    } catch (Exception e) {
      logger.log(e, "Unexpected error while checking downloads");
    }
  }

  private void checkDownload(Download download, List<Rule> matchingRules, long now, DeletionStats stats) {
    final long completedTime = download.getLongAttribute(completedTimeAttribute);
    if (completedTime == 0) {
      download.setLongAttribute(completedTimeAttribute, now);
      return;
    }
    final String category = download.getAttribute(categoryAttribute);
    final Optional<Rule> rule = matchingRules.stream().
        filter(r -> r.getMatcher().matches(category))
        .findAny();
    if (rule.isPresent()) {
      final long duration = now - completedTime;
      final String durationString = TimeUtils.formatDuration(duration);
      int daysTillDelete = (int) (rule.get().getArgAsInt() - TimeUnit.MILLISECONDS.toDays(duration));
      if (daysTillDelete <= 0) {
        logger.log(String.format("Deleting %s after %s", download.getName(), durationString));
        torrentDeleter.deleteDownload(download);
      } else {
        stats.add(daysTillDelete, download.getTorrentSize());
      }
    }
  }

}
