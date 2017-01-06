package com.alon.vuze.vuzemanager.testing;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.download.DownloadManagerListener;
import org.gudy.azureus2.plugins.download.DownloadManagerStats;
import org.gudy.azureus2.plugins.download.DownloadStub;
import org.gudy.azureus2.plugins.download.DownloadStubListener;
import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
import org.gudy.azureus2.plugins.torrent.Torrent;

import java.io.File;
import java.net.URL;
import java.util.Map;

class FakeDownloadManager implements DownloadManager {
  @Override
  public void addDownload(File torrent_file) throws DownloadException {

  }

  @Override
  public void addDownload(URL url) throws DownloadException {

  }

  @Override
  public void addDownload(URL url, boolean auto_download) throws DownloadException {

  }

  @Override
  public void addDownload(URL url, URL referer) {

  }

  @Override
  public void addDownload(URL url, Map request_properties) {

  }

  @Override
  public Download addDownload(Torrent torrent) throws DownloadException {
    return null;
  }

  @Override
  public Download addDownload(Torrent torrent, File torrent_location, File data_location) throws DownloadException {
    return null;
  }

  @Override
  public Download addDownloadStopped(Torrent torrent, File torrent_location, File data_location) throws DownloadException {
    return null;
  }

  @Override
  public Download addNonPersistentDownload(Torrent torrent, File torrent_location, File data_location) throws DownloadException {
    return null;
  }

  @Override
  public Download addNonPersistentDownloadStopped(Torrent torrent, File torrent_location, File data_location) throws DownloadException {
    return null;
  }

  @Override
  public void clearNonPersistentDownloadState(byte[] hash) {

  }

  @Override
  public Download getDownload(Torrent torrent) {
    return null;
  }

  @Override
  public Download getDownload(byte[] hash) throws DownloadException {
    return null;
  }

  @Override
  public Download[] getDownloads() {
    return new Download[0];
  }

  @Override
  public Download[] getDownloads(boolean bSorted) {
    return new Download[0];
  }

  @Override
  public void pauseDownloads() {

  }

  @Override
  public boolean canPauseDownloads() {
    return false;
  }

  @Override
  public void resumeDownloads() {

  }

  @Override
  public boolean canResumeDownloads() {
    return false;
  }

  @Override
  public void startAllDownloads() {

  }

  @Override
  public void stopAllDownloads() {

  }

  @Override
  public DownloadManagerStats getStats() {
    return null;
  }

  @Override
  public boolean isSeedingOnly() {
    return false;
  }

  @Override
  public void addListener(DownloadManagerListener l) {

  }

  @Override
  public void addListener(DownloadManagerListener l, boolean notify_of_current_downloads) {

  }

  @Override
  public void removeListener(DownloadManagerListener l, boolean notify_of_current_downloads) {

  }

  @Override
  public void removeListener(DownloadManagerListener l) {

  }

  @Override
  public void addDownloadWillBeAddedListener(DownloadWillBeAddedListener listener) {

  }

  @Override
  public void removeDownloadWillBeAddedListener(DownloadWillBeAddedListener listener) {

  }

  @Override
  public DownloadEventNotifier getGlobalDownloadEventNotifier() {
    return null;
  }

  @Override
  public void setSaveLocationManager(SaveLocationManager manager) {

  }

  @Override
  public SaveLocationManager getSaveLocationManager() {
    return null;
  }

  @Override
  public DefaultSaveLocationManager getDefaultSaveLocationManager() {
    return null;
  }

  @Override
  public DownloadStub[] getDownloadStubs() {
    return new DownloadStub[0];
  }

  @Override
  public DownloadStub lookupDownloadStub(byte[] hash) {
    return null;
  }

  @Override
  public int getDownloadStubCount() {
    return 0;
  }

  @Override
  public void addDownloadStubListener(DownloadStubListener l, boolean inform_of_current) {

  }

  @Override
  public void removeDownloadStubListener(DownloadStubListener l) {

  }
}
