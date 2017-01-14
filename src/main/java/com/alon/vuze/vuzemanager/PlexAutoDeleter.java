package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.plex.Directory;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.plex.Video;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.Rules;
import com.alon.vuze.vuzemanager.ui.PlexSection;
import com.alon.vuze.vuzemanager.ui.RulesSection;
import com.alon.vuze.vuzemanager.utils.TorrentDeleter;
import com.google.inject.Provider;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alon.vuze.vuzemanager.rules.Rule.Action.WATCHED_AUTO_DELETE;

@Singleton
public class PlexAutoDeleter {
  private static final int LOG_DAYS = 30;

  @Inject
  private Provider<PlexClient> plexClientProvider;

  @Inject
  private Logger logger;

  @Inject
  private TorrentDeleter torrentDeleter;

  @Inject
  private Config config;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @Inject
  private Rules rules;

  @Inject
  private DownloadManager downloadManager;

  @Inject
  public PlexAutoDeleter() {
  }

  public void autoDeleteDownloads() {
    final List<Rule> matchingRules = rules.stream()
        .filter(rule -> rule.getAction() == WATCHED_AUTO_DELETE)
        .collect(Collectors.toList());
    if (matchingRules.size() == 0) {
      return;
    }

    final PlexClient plexClient = plexClientProvider.get();
    try {
      logger.log("");
      logger.log("\n============== Plex Auto Delete Begin ==============");
      logger.log("Fetching show sections from Plex: " + plexClient);
      logger.setStatus("Fetching sections from Plex");
      final Collection<Directory> sections = plexClient.getShowSections();
      logger.log("Found " + sections.size() + " sections");

      final Set<String> filesToDelete = new HashSet<>();
      final Set<String> allFiles = new HashSet<>();
      final String plexRoot = config.get(PlexSection.PLEX_ROOT, "");
      final String vuzeRoot = config.get(PlexSection.VUZE_ROOT, "");

      final long now = System.currentTimeMillis();
      final DeletionStats stats = new DeletionStats(LOG_DAYS);
      logger.setStatus("Fetching episodes from Plex");
      final List<Video> watchedVideos = new ArrayList<>();
      for (Directory section : sections) {
        final Optional<Rule> rule = matchingRules.stream().
            filter(r -> r.getMatcher().matches(section.getTitle()))
            .findAny();
        if (rule.isPresent()) {
          final int days = rule.get().getArgAsInt();
          logger.log("Checking section " + section.getTitle());
          final List<Video> videos = plexClient.getEpisodes(section);
          for (Video video : videos) {
            final ArrayList<String> normalizedFiles = new ArrayList<>();
            for (String file : video.getFiles()) {
              normalizedFiles.add(normalizeFilename(file, plexRoot));
            }
            allFiles.addAll(normalizedFiles);
            if (video.getViewCount() > 0) {
              watchedVideos.add(video);
              final long lastViewedAt = video.getLastViewedAt();
              final long durationMs = now - lastViewedAt;
              final int age = (int) TimeUnit.MILLISECONDS.toDays(durationMs);
              if (age >= days) {
                filesToDelete.addAll(normalizedFiles);
              } else {
                final int daysTillDelete = days - age - 1;
                if (daysTillDelete < LOG_DAYS) {
                  normalizedFiles.forEach(filename -> stats.add(daysTillDelete, new File(filename).length()));
                }
              }
            }
          }
        }
      }
      if (watchedVideos.size() == 0) {
        logger.log("No watched files found");
        return;
      }

      if (filesToDelete.size() > 0) {
        logger.log(String.format("%d episodes will be deleted now", filesToDelete.size()));
      }
      if (stats.getNum(0) > 0) {
        logger.log(String.format("%d GB in %d files will be deleted in 1 day",
            stats.getNum(0), stats.getNumGb(0)));
      }
      for (int i = 1; i < LOG_DAYS; i++) {
        if (stats.getNum(i) > 0) {
          logger.log(String.format("%d GB in %d files will be deleted in %d day",
              stats.getNumGb(i), stats.getNum(i), i));
        }
      }

      checkTorrents(filesToDelete, allFiles, vuzeRoot);
      checkOrphans(filesToDelete, vuzeRoot);
      logger.log("============== Plex Auto Delete End ==============\n");
      logger.log("");
    } catch (Throwable e) {
      logger.log(e, "Error");
    } finally {
      logger.setStatus("Idle");
    }
  }

  private void checkTorrents(Set<String> filesToDelete, Set<String> allFiles, String vuzeRoot) {
    logger.setStatus("Checking torrents");
    final Download[] downloads = downloadManager.getDownloads();
    for (Download download : downloads) {
      boolean isWatched = false;
      boolean servedByPlex = false;
      for (DiskManagerFileInfo info : download.getDiskManagerFileInfo()) {
        final String file = normalizeFilename(info.getFile(true).getPath(), vuzeRoot);
        if (allFiles.contains(file)) {
          servedByPlex = true;
          if (!filesToDelete.contains(file)) {
            isWatched = false;
            break;
          }
          filesToDelete.remove(file);
          isWatched = true;
        }
      }
      if (servedByPlex && isWatched) {
        torrentDeleter.deleteDownload(download);
      }
    }
  }

  private void checkOrphans(Set<String> filesToDelete, String vuzeRoot) {
    logger.setStatus("Checking orphans");
    if (!filesToDelete.isEmpty()) {
      logger.log("Deleting orphans");
      for (String filename : filesToDelete) {
        final File file = new File(vuzeRoot + filename);
        if (!file.exists()) {
          logger.log("File %s doesn't exist. Perhaps it was very deleted recently", file);
          continue;
        }
        logger.log("Deleting %s", file);
        if (config.get(RulesSection.FAKE_DELETE, false)) {
          logger.log("Not actually deleted - see Settings");
        } else {
          final boolean deleted = file.delete();
          if (!deleted) {
            logger.log("Failed to delete file. Maybe it's locked");
          }
        }
      }
    }
  }

  private String normalizeFilename(String file, String root) {
    return file.replace('\\', '/').replace(root, "");
  }

}
