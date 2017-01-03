package com.alon.vuze.vuzemanager.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class WildcardTest {
  @Test
  public void testMatches() throws Exception {
    final Wildcard w1 = new Wildcard("*.jpg");
    assertThat(w1.matches("file.jpg")).isTrue();
    assertThat(w1.matches("file.jpg1")).isFalse();
    assertThat(w1.matches("file.png")).isFalse();

    final Wildcard w2 = new Wildcard("a*.jpg");
    assertThat(w2.matches("a-file.jpg")).isTrue();
    assertThat(w2.matches("file.jpg")).isFalse();

    final Wildcard w3 = new Wildcard("*.*");
    assertThat(w3.matches("file.jpg")).isTrue();
    assertThat(w3.matches("file.png")).isTrue();
    assertThat(w3.matches("file_png")).isFalse();

    final Wildcard w4 = new Wildcard("*?*");
    assertThat(w4.matches("file.jpg")).isTrue();
    assertThat(w4.matches("file.png")).isTrue();
    assertThat(w4.matches("file_png")).isTrue();
  }

}