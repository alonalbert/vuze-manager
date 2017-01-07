package com.alon.vuze.vuzemanager;


import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.torrent.TorrentFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TvEpisode {
  private final String name;
  private final int season;
  private final int episode;
  private final boolean proper;

  private static final Pattern seriesPattern = Pattern.compile("^(?<series>.*)\\.s(?<season>\\d+)e(?<episode>\\d+)");
  private static final Pattern propperPattern = Pattern.compile("\\bproper\\b");

  public static TvEpisode create(Download download) {
    final String name = download.getName().toLowerCase();
    final Matcher matcher = seriesPattern.matcher(name);
    if (!matcher.find()) {
      return null;
    }
    final String series = matcher.group("series").replace('.', ' ');
    final int season = Integer.parseInt(matcher.group("season"));
    final int episode = Integer.parseInt(matcher.group("episode"));
    final boolean proper = checkIfProper(name, download.getTorrent().getFiles());
    return new TvEpisode(series, season, episode, proper);
  }

  private static boolean checkIfProper(String name, TorrentFile[] files) {
    if (propperPattern.matcher(name).find()) {
      return true;
    }
    for (TorrentFile file : files) {
      if (propperPattern.matcher(file.getName().toLowerCase()).find()) {
        return true;
      }
    }
    return false;
  }

  private TvEpisode(String name, int season, int episode, boolean proper) {
    this.name = name;
    this.season = season;
    this.episode = episode;
    this.proper = proper;
  }

  @Override
  public String toString() {
    final String string = String.format("%s - S%02dE%02d", name, season, episode);
    return proper ? string + " (proper)" : string;
  }

  public boolean isProper() {
    return proper;
  }

  public boolean isSameEpisode(TvEpisode other) {
    return other != null && name.equals(other.name) && season == other.season && episode == other.episode;
  }
}
