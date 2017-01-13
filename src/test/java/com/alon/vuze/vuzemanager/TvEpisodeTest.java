package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.testing.FakeDownload;
import com.alon.vuze.vuzemanager.testing.FakeTorrent;
import com.google.common.truth.Truth;
import org.junit.Test;

public class TvEpisodeTest {
  @Test
  public void testCreate() throws Exception {
    final TvEpisode episode = TvEpisode.create(
        new FakeDownload("Stephen.Colbert.2017.01.12.Tom.Selleck.HDTV.x264-BRISK",
            new FakeTorrent("", new String[]{})));
    Truth.assertThat(episode).isNotNull();
    Truth.assertThat(episode.getName()).isEqualTo("Stephen Colbert");
    Truth.assertThat(episode.getSeason()).isEqualTo(2017);
    Truth.assertThat(episode.getEpisode()).isEqualTo(112);
  }

}