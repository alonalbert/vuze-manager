package com.alon.vuze.vuzemanager.testing;

import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLList;
import org.gudy.azureus2.plugins.torrent.TorrentEncodingException;
import org.gudy.azureus2.plugins.torrent.TorrentException;
import org.gudy.azureus2.plugins.torrent.TorrentFile;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class FakeTorrent implements Torrent {

  private final String name;
  private final String[] files;

  public FakeTorrent(String name, String[] files) {
    this.name = name;
    this.files = files;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public URL getAnnounceURL() {
    return null;
  }

  @Override
  public void setAnnounceURL(URL url) {

  }

  @Override
  public TorrentAnnounceURLList getAnnounceURLList() {
    return null;
  }

  @Override
  public byte[] getHash() {
    return new byte[0];
  }

  @Override
  public long getSize() {
    return 0;
  }

  @Override
  public String getComment() {
    return null;
  }

  @Override
  public void setComment(String comment) {

  }

  @Override
  public long getCreationDate() {
    return 0;
  }

  @Override
  public String getCreatedBy() {
    return null;
  }

  @Override
  public long getPieceSize() {
    return 0;
  }

  @Override
  public long getPieceCount() {
    return 0;
  }

  @Override
  public byte[][] getPieces() {
    return new byte[0][];
  }

  @Override
  public TorrentFile[] getFiles() {
    return new TorrentFile[0];
  }

  @Override
  public String getEncoding() {
    return null;
  }

  @Override
  public void setEncoding(String encoding) throws TorrentEncodingException {

  }

  @Override
  public void setDefaultEncoding() throws TorrentEncodingException {

  }

  @Override
  public Object getAdditionalProperty(String name) {
    return null;
  }

  @Override
  public Torrent removeAdditionalProperties() {
    return null;
  }

  @Override
  public void setPluginStringProperty(String name, String value) {

  }

  @Override
  public String getPluginStringProperty(String name) {
    return null;
  }

  @Override
  public void setMapProperty(String name, Map value) {

  }

  @Override
  public Map getMapProperty(String name) {
    return null;
  }

  @Override
  public boolean isDecentralised() {
    return false;
  }

  @Override
  public boolean isDecentralisedBackupEnabled() {
    return false;
  }

  @Override
  public void setDecentralisedBackupRequested(boolean requested) {

  }

  @Override
  public boolean isDecentralisedBackupRequested() {
    return false;
  }

  @Override
  public boolean isPrivate() {
    return false;
  }

  @Override
  public void setPrivate(boolean priv) {

  }

  @Override
  public boolean wasCreatedByUs() {
    return false;
  }

  @Override
  public URL getMagnetURI() throws TorrentException {
    return null;
  }

  @Override
  public Map writeToMap() throws TorrentException {
    return null;
  }

  @Override
  public void writeToFile(File file) throws TorrentException {

  }

  @Override
  public byte[] writeToBEncodedData() throws TorrentException {
    return new byte[0];
  }

  @Override
  public void save() throws TorrentException {

  }

  @Override
  public void setComplete(File data_dir) throws TorrentException {

  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public boolean isSimpleTorrent() {
    return false;
  }

  @Override
  public Torrent getClone() throws TorrentException {
    return null;
  }
}
