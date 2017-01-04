package com.alon.vuze.vuzemanager.config;

import com.alon.vuze.vuzemanager.Rule;
import com.alon.vuze.vuzemanager.Rule.Action;
import com.alon.vuze.vuzemanager.logger.DebugLogger;
import com.google.common.collect.Lists;
import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * todo
 */
public class ConfigTest {

  private static final String KEY = "key";
  private static final String NOKEY = "nokey";
  private Config config;

  @Before
  public void setUp() throws Exception {
    config = new Config("test", new DebugLogger());
  }

  @Test
  public void testInt() throws Exception {
    final int value = 1;
    final int defaultValue = -1;
    config.set(KEY, value);
    Truth.assertThat(config.get(KEY, Integer.class)).isEqualTo(value);
    Truth.assertThat(config.get(KEY, defaultValue)).isEqualTo(value);
    Truth.assertThat(config.get(NOKEY, Integer.class)).isEqualTo(null);
    Truth.assertThat(config.get(NOKEY, defaultValue)).isEqualTo(defaultValue);
  }

  @Test
  public void testString() throws Exception {
    final String value = "value";
    final String defaultValue = "default";
    config.set(KEY, value);
    Truth.assertThat(config.get(KEY, value.getClass())).isEqualTo(value);
    Truth.assertThat(config.get(KEY, defaultValue)).isEqualTo(value);
    Truth.assertThat(config.get(NOKEY, value.getClass())).isEqualTo(null);
    Truth.assertThat(config.get(NOKEY, defaultValue)).isEqualTo(defaultValue);
  }

  @Test
  public void testRule() throws Exception {
    final Rule value = new Rule("q", Action.AUTO_DESTINATION, "a");
    config.set(KEY, value);
    Truth.assertThat(config.get(KEY, value.getClass())).isEqualTo(value);
    Truth.assertThat(config.get(NOKEY, value.getClass())).isEqualTo(null);
  }

  @Test
  public void testListOfStrings() throws Exception {
    final List<String> value = Lists.newArrayList("1", "2");
    final List<String> defaultValue = Lists.newArrayList();
    config.set(KEY, value);
    Truth.assertThat(config.get(KEY, value.getClass())).isEqualTo(value);
    Truth.assertThat(config.get(KEY, defaultValue)).isEqualTo(value);
    Truth.assertThat(config.get(NOKEY, value.getClass())).isEqualTo(null);
    Truth.assertThat(config.get(NOKEY, defaultValue)).isEqualTo(defaultValue);
  }
}