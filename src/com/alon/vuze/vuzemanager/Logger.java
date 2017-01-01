package com.alon.vuze.vuzemanager;

interface Logger {
  void log(String format, Object... args);

  void log(Throwable e, String format, Object... args);
}
