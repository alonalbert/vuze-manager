package com.alon.vuze.vuzemanager.testing;

import org.gudy.azureus2.plugins.torrent.TorrentFile;

public class FakeTorrentFile implements TorrentFile {
  private final String name;

  public FakeTorrentFile(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public long getSize() {
    return 0;
  }
}
