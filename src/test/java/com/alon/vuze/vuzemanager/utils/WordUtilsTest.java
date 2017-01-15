package com.alon.vuze.vuzemanager.utils;

import com.google.common.truth.Truth;
import org.junit.Test;

public class WordUtilsTest {
  @Test
  public void testCapitalizeFully() throws Exception {
    Truth.assertThat(WordUtils.titleCase("the man in the high castle")).isEqualTo("The Man in the High Castle");
    Truth.assertThat(WordUtils.titleCase("Last Man Standing US")).isEqualTo("Last Man Standing US");
    Truth.assertThat(WordUtils.titleCase("A Series of Unfortunate Events")).isEqualTo("A Series of Unfortunate Events");
  }

}