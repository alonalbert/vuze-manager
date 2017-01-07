package com.alon.vuze.vuzemanager.testing;

import com.alon.vuze.vuzemanager.TvEpisode;
import org.gudy.azureus2.plugins.torrent.Torrent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DetectTvShows {
  public static void main(String[] args) {
    final Collection<Torrent> torrents = getTorrents(args[0]);
    for (Torrent torrent : torrents) {
      final TvEpisode show = TvEpisode.create(new FakeDownload(torrent.getName(), torrent));
      if (show != null && show.isProper()) {
        System.out.println(show);
      }
    }
  }

  private static Collection<Torrent> getTorrents(String path) {
    final LinkedList<Torrent> torrents = new LinkedList<>();
    final File root = new File(path);

    final File[] files = root.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isFile()) {
          torrents.add(new FakeTorrent(file.getName(), new String[]{file.getName()}));
        } else {
          torrents.add(new FakeTorrent(file.getName(), getFiles(file)));
        }
      }
    }
    torrents.sort(Comparator.comparing(o -> o.getName().toLowerCase()));
    return torrents;
  }

  private static String[] getFiles(File root) {
    final List<String> files = new ArrayList<>();
    collectFiles(root, files);
    return files.toArray(new String[files.size()]);
  }

  private static void collectFiles(File root, List<String> output) {
    final File[] files = root.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isFile()) {
          output.add(file.getName());
        } else {
          collectFiles(file, output);
        }
      }
    }
  }
}
