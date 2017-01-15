package com.alon.vuze.vuzemanager;


import com.alon.vuze.vuzemanager.utils.WordUtils;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.torrent.TorrentFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TvEpisode {
  private final String name;
  private final int season;
  private final int episode;
  private final boolean proper;

  private static final Pattern[] seriesPattern = new Pattern[] {
      Pattern.compile("^(?<series>.*?)(\\.\\d\\d\\d\\d)?\\.s(?<season>\\d+)e(?<episode>\\d+)", Pattern.CASE_INSENSITIVE),
      Pattern.compile("^(?<series>.*)\\.(?<season>\\d\\d\\d\\d)\\.(?<episode>\\d\\d\\.\\d\\d).*HDTV", Pattern.CASE_INSENSITIVE),
  };
  private static final Pattern propperPattern = Pattern.compile("\\bproper\\b", Pattern.CASE_INSENSITIVE);

  public static TvEpisode create(Download download) {
    final String name = download.getName();
    for (Pattern pattern : seriesPattern) {
      final Matcher matcher = pattern.matcher(name);
      if (!matcher.find()) {
        continue;
      }
      final String series = WordUtils.titleCase(matcher.group("series").replace('.', ' '));
      final int season = Integer.parseInt(matcher.group("season"));
      final int episode = Integer.parseInt(matcher.group("episode").replace(".", ""));
      final boolean proper = checkIfProper(name, download.getTorrent().getFiles());
      return new TvEpisode(series, season, episode, proper);
    }
    return null;
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

  public String getName() {
    return name;
  }

  public int getSeason() {
    return season;
  }

  public int getEpisode() {
    return episode;
  }

  public boolean isSameEpisode(TvEpisode other) {
    return other != null && name.equalsIgnoreCase(other.name) && season == other.season && episode == other.episode;
  }
}
