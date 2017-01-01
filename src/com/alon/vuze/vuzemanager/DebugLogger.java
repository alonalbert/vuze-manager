package com.alon.vuze.vuzemanager;

public class DebugLogger implements Logger {
  @Override
  public void log(String format, Object... args) {
    System.out.printf(format + "\n", (Object[]) args);
  }

  @Override
  public void log(Throwable e, String format, Object... args) {
    System.out.printf(format + "\n", (Object[]) args);
    e.printStackTrace();
  }
}
