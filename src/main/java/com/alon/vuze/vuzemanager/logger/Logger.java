package com.alon.vuze.vuzemanager.logger;

public interface Logger {
  void log(String format, Object... args);

  void log(Throwable e, String format, Object... args);

  void setStatus(String title);
}
