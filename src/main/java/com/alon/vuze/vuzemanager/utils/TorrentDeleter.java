package com.alon.vuze.vuzemanager.utils;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.ui.RulesSection;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class TorrentDeleter {

  @Inject
  private Logger logger;

  @Inject
  private Config config;

  @Inject
  public TorrentDeleter() {
  }

  public void deleteDownload(Download download) {
    logger.log("Deleting %s", download.getName());
    if (config.get(RulesSection.FAKE_DELETE, false)) {
      logger.log("Not actually deleted - see Settings");
      return;
    }

    if (download.getState() == Download.ST_STOPPED) {
      removeDownload(download);
    } else {
      download.addListener(new DownloadListener() {
        @Override
        public void stateChanged(Download download, int oldState, int newState) {
          if (newState == Download.ST_STOPPED) {
            removeDownload(download);
          }
        }

        @Override
        public void positionChanged(Download download, int oldPosition, int newPosition) {
          // noop
        }
      });
      try {
        download.stop();
      } catch (DownloadException e) {
        logger.log(e, "Error");
      }
    }
  }

  private void removeDownload(Download download) {
    try {
      download.remove(true, true);
    } catch (DownloadException | DownloadRemovalVetoException e) {
      logger.log(e, "Error");
    }
  }

}
