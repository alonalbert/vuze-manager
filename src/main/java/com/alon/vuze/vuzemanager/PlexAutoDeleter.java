package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.plex.Directory;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.plex.Video;
import com.google.inject.Inject;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class PlexAutoDeleter {
  private static final int LOG_DAYS = 30;
  @SuppressWarnings("unused")
  @Inject
  private PlexClient plexClient;

  @SuppressWarnings("unused")
  @Inject
  private Logger logger;

  @SuppressWarnings("unused")
  @Inject
  private Config config;

  @SuppressWarnings("unused")
  @Inject
  private DownloadManager downloadManager;

  @SuppressWarnings("unused")
  @Inject
  public PlexAutoDeleter() {
  }

  void autoDeleteDownloads() {
    try {
      logger.log("Fetching show sections from Plex: " + plexClient);
      logger.setStatus("Fetching sections from Plex");
      final Collection<Directory> sections = plexClient.getShowSections();
      logger.log("Found " + sections.size() + " sections");

      final List<Rule> rules = RulesView.getRulesFromConfig(config).stream()
          .filter(category -> category.getAction() == Rule.Action.WATCHED_AUTO_DELETE)
          .collect(Collectors.toList());

      final Set<String> filesToDelete = new HashSet<>();
      final Set<String> allFiles = new HashSet<>();
      final String plexRoot = ""; // todo
      final String vuzeRoot = ""; //todo

      final long now = System.currentTimeMillis();
      final int[] byDay = new int[LOG_DAYS];
      logger.setStatus("Fetching episodes from Plex");
      final List<Video> watchedVideos = new ArrayList<>();
      for (Directory section : sections) {
        final int days = getDaysIfMatch(section.getTitle(), rules);
        if (days > 0) {
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
//              for (String file : normalizedFiles) {
//                logger.log("Video %s watched %s ago", new File(file).getName(), TimeUtils.formatDuration(durationMs));
//              }
              if (age >= days) {
                filesToDelete.addAll(normalizedFiles);
              } else {
                final int daysTillDelete = days - age;
                if (daysTillDelete < LOG_DAYS) {
                  byDay[daysTillDelete]++;
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
      if (byDay[0] > 0) {
        logger.log(String.format("%d episodes will be deleted in 1 day", byDay[0]));
      }
      for (int i = 1; i < LOG_DAYS; i++) {
        if (byDay[i] > 0) {
          logger.log(String.format("%d episodes will be deleted in %d days", byDay[i], i + 1));
        }
      }

      checkTorrents(filesToDelete, allFiles, vuzeRoot);
      checkOrphans(filesToDelete, vuzeRoot);
      logger.log("Done!!!");
    } catch (Throwable e) {
      logger.log(e, "Error");
    } finally {
      logger.setStatus("Idle");
    }
  }

  private int getDaysIfMatch(String title, List<Rule> rules) {
    for (Rule rule : rules) {
      if (rule.getMatcher().matches(title)) {
        return rule.getArgAsInt();
      }
    }
    return -1;
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
        if (download.getState() == Download.ST_STOPPED) {
          logger.log("    Deleting " + download.getTorrentFileName());
          removeDownload(download);
        } else {
          download.addListener(new DownloadListener() {
            @Override
            public void stateChanged(Download download, int oldState, int newState) {
              if (newState == Download.ST_STOPPED) {
                logger.log("    Deleting " + download.getTorrentFileName());
                removeDownload(download);
              }
            }

            @Override
            public void positionChanged(Download download, int oldPosition, int newPosition) {
              // noop
            }
          });
          try {
            logger.log("    Stopping " + download.getTorrentFileName());
            if (false) { // todo
              download.stop();
            }
          } catch (DownloadException e) {
            logger.log("Error", e);
          }
        }
      }
    }
  }

  private void checkOrphans(Set<String> filesToDelete, String vuzeRoot) {
    logger.setStatus("Checking orphans");
    if (!filesToDelete.isEmpty()) {
      logger.log("Deleting orphans");
      for (String filename : filesToDelete) {
        final File file = new File(vuzeRoot + filename);
        logger.log("    Deleting " + file);
        if (false) { // todo
          final boolean deleted = file.delete();
          logger.log("    Deleted: " + deleted);
        }
      }
    }
  }

  private void removeDownload(Download download) {
    try {
      if (false) { // todo
        download.remove(true, true);
      }
    } catch (DownloadException | DownloadRemovalVetoException e) {
      logger.log("Error", e);
    }
  }

  private String normalizeFilename(String file, String root) {
    return file.replace('\\', '/').replace(root, "");
  }

}
