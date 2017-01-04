package com.alon.vuze.vuzemanager.config;

import com.alon.vuze.vuzemanager.logger.DebugLogger;
import org.junit.Before;
import org.junit.Test;

/**
 * todo
 */
public class ConfigTest {

  private Config config;

  @Before
  public void setUp() throws Exception {
    config = new Config("test", new DebugLogger());
  }

  @Test
  public void testInt() throws Exception {
  }

}