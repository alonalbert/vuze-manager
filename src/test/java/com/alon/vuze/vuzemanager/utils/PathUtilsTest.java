package com.alon.vuze.vuzemanager.utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class PathUtilsTest {
  @Test
  public void testGetSaveRoot() throws Exception {
    assertThat(
        PathUtils.getSaveRoot("/nas/video/tv/Grimm.S06E02.REPACK.720p.HDTV.x264-AVS/Grimm.S06E02.REPACK.720p.HDTV.x264-AVS"))
        .isEqualTo("/nas/video/tv");
    assertThat(
        PathUtils.getSaveRoot("/nas/video/tv/Grimm.S06E02.REPACK.720p.HDTV.x264-AVS"))
        .isEqualTo("/nas/video/tv");
  }

}