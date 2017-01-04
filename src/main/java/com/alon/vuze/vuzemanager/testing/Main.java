package com.alon.vuze.vuzemanager.testing;

import com.alon.vuze.vuzemanager.Rule;
import com.alon.vuze.vuzemanager.Rule.Action;
import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.DebugLogger;
import java.io.IOException;

class Main {


  public static void main(String[] args) throws IOException {

    final Config config = new Config("test", new DebugLogger());

    config.set("rule", new Rule("q", Action.AUTO_DESTINATION, "a"));

    System.out.println(config.get("rule", Rule.class));

  }
}