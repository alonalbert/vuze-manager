package com.alon.vuze.vuzemanager;

// TODO: 12/31/16 log to panel
class DebugLogger {
  void log(String format, Object... args) {
    System.out.printf(format + "\n", (Object[]) args);
  }
  void log(Throwable e, String format, Object... args) {
    System.out.printf(format + "\n", (Object[]) args);
    e.printStackTrace();
  }
}
