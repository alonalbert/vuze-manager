package com.alon.vuze.vuzemanager.utils;

import com.alon.vuze.vuzemanager.logger.Logger;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.alon.vuze.vuzemanager.VuzeManagerPlugin.FAKE_DELETE;


@Singleton
public class TorrentDeleter {

  @Inject
  private Logger logger;

  @Named(FAKE_DELETE)
  @javax.inject.Inject
  private
  Provider<Boolean> fakeDeleteProvider;


  @Inject
  public TorrentDeleter() {
  }

  public void deleteDownload(Download download) {
    logger.log("Deleting %s", download.getName());
    if (fakeDeleteProvider.get()) {
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
