package com.alon.vuze.vuzemanager.testing;

import org.gudy.azureus2.plugins.torrent.TorrentAttributeListener;

class MockTorrentAttribute implements org.gudy.azureus2.plugins.torrent.TorrentAttribute {
  @Override
  public String getName() {
    return null;
  }

  @Override
  public String[] getDefinedValues() {
    return new String[0];
  }

  @Override
  public void addDefinedValue(String name) {

  }

  @Override
  public void removeDefinedValue(String name) {

  }

  @Override
  public void addTorrentAttributeListener(TorrentAttributeListener l) {

  }

  @Override
  public void removeTorrentAttributeListener(TorrentAttributeListener l) {

  }
}
