package com.alon.vuze.vuzemanager.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

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

}