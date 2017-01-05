package com.alon.vuze.vuzemanager.utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class WildcardMatcherTest {
  @Test
  public void testMatches() throws Exception {
    final WildcardMatcher w1 = new WildcardMatcher("*.jpg");
    assertThat(w1.matches("file.jpg")).isTrue();
    assertThat(w1.matches("file.jpg1")).isFalse();
    assertThat(w1.matches("file.png")).isFalse();

    final WildcardMatcher w2 = new WildcardMatcher("a*.jpg");
    assertThat(w2.matches("a-file.jpg")).isTrue();
    assertThat(w2.matches("file.jpg")).isFalse();

    final WildcardMatcher w3 = new WildcardMatcher("*.*");
    assertThat(w3.matches("file.jpg")).isTrue();
    assertThat(w3.matches("file.png")).isTrue();
    assertThat(w3.matches("file_png")).isFalse();

    final WildcardMatcher w4 = new WildcardMatcher("*?*");
    assertThat(w4.matches("file.jpg")).isTrue();
    assertThat(w4.matches("file.png")).isTrue();
    assertThat(w4.matches("file_png")).isTrue();
  }

  @Test
  public void testCaseInsensitive() throws Exception {
    final WildcardMatcher w1 = new WildcardMatcher("a");
    assertThat(w1.matches("A")).isTrue();
    assertThat(w1.matches("a")).isTrue();
    final WildcardMatcher w2 = new WildcardMatcher("A");
    assertThat(w1.matches("A")).isTrue();
    assertThat(w1.matches("a")).isTrue();
  }

  @Test
  public void testDigits() throws Exception {
    final WildcardMatcher w1 = new WildcardMatcher("*.s##e##.*");
    assertThat(w1.matches("Series.S01E12.HDTV")).isTrue();
    assertThat(w1.matches("Series.SO1E12.HDTV")).isFalse();
    assertThat(w1.matches("Series.s01E12.HDTV")).isTrue();
  }

}