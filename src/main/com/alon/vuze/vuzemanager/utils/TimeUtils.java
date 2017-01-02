package com.alon.vuze.vuzemanager.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
  public static String formatDuration(long durationMs) {
    final long time = TimeUnit.MILLISECONDS.toMinutes(durationMs);
    final long minutes = time % 60;
    final long hours = time / 60 % 24;
    final long days = time / 60 / 24;
    final StringBuilder timeString = new StringBuilder();
    if (days > 0) {
      timeString.append(String.format("%dd", days));
    }
    if (hours > 0) {
      timeString.append(String.format(" %dh", hours));
    }
    if (days == 0 && minutes > 0) {
      timeString.append(String.format(" %dm", minutes));
    }
    if (timeString.length() == 0) {
      timeString.append("0m");
    }
    return timeString.toString().trim();
  }
}
