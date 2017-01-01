package com.alon.vuze.vuzemanager;

// TODO: 12/31/16 log to panel
public class DebugLogger {
  public void log(String format, Object... args) {
    System.out.printf(format + "\n", (Object[]) args);
  }
  public void log(Throwable e, String format, Object... args) {
    // TODO: 12/31/16 log to panel
    System.out.printf(format + "\n", (Object[]) args);
    e.printStackTrace();
  }
}
