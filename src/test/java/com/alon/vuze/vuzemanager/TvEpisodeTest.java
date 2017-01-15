package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.testing.FakeDownload;
import com.alon.vuze.vuzemanager.testing.FakeTorrent;
import com.google.common.truth.Truth;
import org.junit.Test;

public class TvEpisodeTest {
  @Test
  public void testByDate() throws Exception {
    final TvEpisode episode = TvEpisode.create(
        new FakeDownload("Stephen.Colbert.2017.01.12.Tom.Selleck.HDTV.x264-BRISK",
            new FakeTorrent("", new String[]{})));
    Truth.assertThat(episode).isNotNull();
    Truth.assertThat(episode.getName()).isEqualTo("Stephen Colbert");
    Truth.assertThat(episode.getSeason()).isEqualTo(2017);
    Truth.assertThat(episode.getEpisode()).isEqualTo(112);
  }

  @Test
  public void testBySeries() throws Exception {
    final TvEpisode episode = TvEpisode.create(
        new FakeDownload("A.Series.of.Unfortunate.Events.S01E04.720p.NF.WEBRip.DD5.1.x264-ViSUM",
            new FakeTorrent("", new String[]{})));
    Truth.assertThat(episode).isNotNull();
    Truth.assertThat(episode.getName()).isEqualTo("A Series of Unfortunate Events");
    Truth.assertThat(episode.getSeason()).isEqualTo(1);
    Truth.assertThat(episode.getEpisode()).isEqualTo(4);
  }

  @Test
  public void testBySeriesRemoveDate() throws Exception {
    final TvEpisode episode = TvEpisode.create(
        new FakeDownload("Taboo.2017.S01E01.720p.WEB-DL.DD5.1.H264-R2D2",
            new FakeTorrent("", new String[]{})));
    Truth.assertThat(episode).isNotNull();
    Truth.assertThat(episode.getName()).isEqualTo("Taboo");
    Truth.assertThat(episode.getSeason()).isEqualTo(1);
    Truth.assertThat(episode.getEpisode()).isEqualTo(1);
  }

}