package com.alon.vuze.vuzemanager.utils;

import com.google.common.truth.Truth;
import org.junit.Test;

public class WordUtilsTest {
  @Test
  public void testCapitalizeFully() throws Exception {
    Truth.assertThat(WordUtils.titleCase("the man in the high castle")).isEqualTo("The Man in the High Castle");
  }

}