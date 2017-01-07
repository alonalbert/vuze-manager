package com.alon.vuze.vuzemanager.testing;

import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
import org.gudy.azureus2.plugins.disk.DiskManager;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
import org.gudy.azureus2.plugins.download.DownloadActivationListener;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadPeerListener;
import org.gudy.azureus2.plugins.download.DownloadPropertyListener;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
import org.gudy.azureus2.plugins.download.DownloadStats;
import org.gudy.azureus2.plugins.download.DownloadStub;
import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
import org.gudy.azureus2.plugins.download.DownloadWillBeRemovedListener;
import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
import org.gudy.azureus2.plugins.network.RateLimiter;
import org.gudy.azureus2.plugins.peers.PeerManager;
import org.gudy.azureus2.plugins.tag.Tag;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FakeDownload implements Download {
  private final String name;
  private final Torrent torrent;

  public FakeDownload(String name, Torrent torrent) {
    this.name = name;
    this.torrent = torrent;
  }

  @Override
  public int getState() {
    return 0;
  }

  @Override
  public int getSubState() {
    return 0;
  }

  @Override
  public String getErrorStateDetails() {
    return null;
  }

  @Override
  public boolean getFlag(long flag) {
    return false;
  }

  @Override
  public void setFlag(long flag, boolean set) {

  }

  @Override
  public long getFlags() {
    return 0;
  }

  @Override
  public int getIndex() {
    return 0;
  }

  @Override
  public Torrent getTorrent() {
    return torrent;
  }

  @Override
  public void initialize() throws DownloadException {

  }

  @Override
  public void start() throws DownloadException {

  }

  @Override
  public void stop() throws DownloadException {

  }

  @Override
  public void stopAndQueue() throws DownloadException {

  }

  @Override
  public void restart() throws DownloadException {

  }

  @Override
  public void recheckData() throws DownloadException {

  }

  @Override
  public boolean isStartStopLocked() {
    return false;
  }

  @Override
  public boolean isForceStart() {
    return false;
  }

  @Override
  public void setForceStart(boolean forceStart) {

  }

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  public void setPriority(int priority) {

  }

  @Override
  public boolean isPriorityLocked() {
    return false;
  }

  @Override
  public boolean isPaused() {
    return false;
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public boolean isStub() {
    return false;
  }

  @Override
  public Download destubbify() throws DownloadException {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public byte[] getTorrentHash() {
    return new byte[0];
  }

  @Override
  public long getTorrentSize() {
    return 0;
  }

  @Override
  public String getTorrentFileName() {
    return null;
  }

  @Override
  public String getAttribute(TorrentAttribute attribute) {
    return null;
  }

  @Override
  public void setAttribute(TorrentAttribute attribute, String value) {

  }

  @Override
  public String[] getListAttribute(TorrentAttribute attribute) {
    return new String[0];
  }

  @Override
  public void setListAttribute(TorrentAttribute attribute, String[] value) {

  }

  @Override
  public void setMapAttribute(TorrentAttribute attribute, Map value) {

  }

  @Override
  public Map getMapAttribute(TorrentAttribute attribute) {
    return null;
  }

  @Override
  public int getIntAttribute(TorrentAttribute attribute) {
    return 0;
  }

  @Override
  public void setIntAttribute(TorrentAttribute attribute, int value) {

  }

  @Override
  public long getLongAttribute(TorrentAttribute attribute) {
    return 0;
  }

  @Override
  public void setLongAttribute(TorrentAttribute attribute, long value) {

  }

  @Override
  public boolean getBooleanAttribute(TorrentAttribute attribute) {
    return false;
  }

  @Override
  public void setBooleanAttribute(TorrentAttribute attribute, boolean value) {

  }

  @Override
  public boolean hasAttribute(TorrentAttribute attribute) {
    return false;
  }

  @Override
  public String getCategoryName() {
    return null;
  }

  @Override
  public void setCategory(String sName) {

  }

  @Override
  public List<Tag> getTags() {
    return null;
  }

  @Override
  public void remove() throws DownloadException, DownloadRemovalVetoException {

  }

  @Override
  public void remove(boolean delete_torrent, boolean delete_data) throws DownloadException, DownloadRemovalVetoException {

  }

  @Override
  public int getPosition() {
    return 0;
  }

  @Override
  public long getCreationTime() {
    return 0;
  }

  @Override
  public void setPosition(int newPosition) {

  }

  @Override
  public void moveUp() {

  }

  @Override
  public void moveDown() {

  }

  @Override
  public void moveTo(int position) {

  }

  @Override
  public boolean canBeRemoved() throws DownloadRemovalVetoException {
    return false;
  }

  @Override
  public void setAnnounceResult(DownloadAnnounceResult result) {

  }

  @Override
  public void setScrapeResult(DownloadScrapeResult result) {

  }

  @Override
  public DownloadAnnounceResult getLastAnnounceResult() {
    return null;
  }

  @Override
  public DownloadScrapeResult getLastScrapeResult() {
    return null;
  }

  @Override
  public DownloadScrapeResult getAggregatedScrapeResult() {
    return null;
  }

  @Override
  public DownloadActivationEvent getActivationState() {
    return null;
  }

  @Override
  public DownloadStats getStats() {
    return null;
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  public void setMaximumDownloadKBPerSecond(int kb) {

  }

  @Override
  public int getMaximumDownloadKBPerSecond() {
    return 0;
  }

  @Override
  public int getUploadRateLimitBytesPerSecond() {
    return 0;
  }

  @Override
  public void setUploadRateLimitBytesPerSecond(int max_rate_bps) {

  }

  @Override
  public int getDownloadRateLimitBytesPerSecond() {
    return 0;
  }

  @Override
  public void setDownloadRateLimitBytesPerSecond(int max_rate_bps) {

  }

  @Override
  public void addRateLimiter(RateLimiter limiter, boolean is_upload) {

  }

  @Override
  public void removeRateLimiter(RateLimiter limiter, boolean is_upload) {

  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public boolean isComplete(boolean bIncludeDND) {
    return false;
  }

  @Override
  public boolean isChecking() {
    return false;
  }

  @Override
  public boolean isMoving() {
    return false;
  }

  @Override
  public String getSavePath() {
    return null;
  }

  @Override
  public DownloadStubFile[] getStubFiles() {
    return new DownloadStubFile[0];
  }

  @Override
  public void moveDataFiles(File new_parent_dir) throws DownloadException {

  }

  @Override
  public void moveDataFiles(File new_parent_dir, String new_name) throws DownloadException {

  }

  @Override
  public void moveTorrentFile(File new_parent_dir) throws DownloadException {

  }

  @Override
  public void renameDownload(String name) throws DownloadException {

  }

  @Override
  public PeerManager getPeerManager() {
    return null;
  }

  @Override
  public DiskManager getDiskManager() {
    return null;
  }

  @Override
  public DiskManagerFileInfo[] getDiskManagerFileInfo() {
    return new DiskManagerFileInfo[0];
  }

  @Override
  public DiskManagerFileInfo getDiskManagerFileInfo(int index) {
    return null;
  }

  @Override
  public int getDiskManagerFileCount() {
    return 0;
  }

  @Override
  public void requestTrackerAnnounce() {

  }

  @Override
  public void requestTrackerAnnounce(boolean immediate) {

  }

  @Override
  public void requestTrackerScrape(boolean immediate) {

  }

  @Override
  public int getSeedingRank() {
    return 0;
  }

  @Override
  public void setSeedingRank(int rank) {

  }

  @Override
  public byte[] getDownloadPeerId() {
    return new byte[0];
  }

  @Override
  public boolean isMessagingEnabled() {
    return false;
  }

  @Override
  public void setMessagingEnabled(boolean enabled) {

  }

  @Override
  public File[] calculateDefaultPaths(boolean for_moving) {
    return new File[0];
  }

  @Override
  public boolean isInDefaultSaveDir() {
    return false;
  }

  @Override
  public boolean isRemoved() {
    return false;
  }

  @Override
  public boolean canMoveDataFiles() {
    return false;
  }

  @Override
  public SaveLocationChange calculateDefaultDownloadLocation() {
    return null;
  }

  @Override
  public void changeLocation(SaveLocationChange slc) throws DownloadException {

  }

  @Override
  public Object getUserData(Object key) {
    return null;
  }

  @Override
  public void setUserData(Object key, Object data) {

  }

  @Override
  public void startDownload(boolean force) {

  }

  @Override
  public void stopDownload() {

  }

  @Override
  public boolean canStubbify() {
    return false;
  }

  @Override
  public DownloadStub stubbify() throws DownloadException, DownloadRemovalVetoException {
    return null;
  }

  @Override
  public List<DistributedDatabase> getDistributedDatabases() {
    return null;
  }

  @Override
  public DiskManagerFileInfo getPrimaryFile() {
    return null;
  }

  @Override
  public void addListener(DownloadListener l) {

  }

  @Override
  public void removeListener(DownloadListener l) {

  }

  @Override
  public void addTrackerListener(DownloadTrackerListener l) {

  }

  @Override
  public void addTrackerListener(DownloadTrackerListener l, boolean immediateTrigger) {

  }

  @Override
  public void removeTrackerListener(DownloadTrackerListener l) {

  }

  @Override
  public void addDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l) {

  }

  @Override
  public void removeDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l) {

  }

  @Override
  public void addActivationListener(DownloadActivationListener l) {

  }

  @Override
  public void removeActivationListener(DownloadActivationListener l) {

  }

  @Override
  public void addPeerListener(DownloadPeerListener l) {

  }

  @Override
  public void removePeerListener(DownloadPeerListener l) {

  }

  @Override
  public void addPropertyListener(DownloadPropertyListener l) {

  }

  @Override
  public void removePropertyListener(DownloadPropertyListener l) {

  }

  @Override
  public void addAttributeListener(DownloadAttributeListener l, TorrentAttribute attr, int event_type) {

  }

  @Override
  public void removeAttributeListener(DownloadAttributeListener l, TorrentAttribute attr, int event_type) {

  }

  @Override
  public void addCompletionListener(DownloadCompletionListener l) {

  }

  @Override
  public void removeCompletionListener(DownloadCompletionListener l) {

  }
}
