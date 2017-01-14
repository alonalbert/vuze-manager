package com.alon.vuze.vuzemanager.utils;

import java.util.Arrays;

public class PathUtils {
  public static String getSaveRoot(String path) {
    final String[] split = path.split("/");
    final String s1 = split[split.length - 1];
    final String s2 = split[split.length - 2];
    final int n;
    if (s2.equals(s1)) {
      n = split.length - 2;
    } else {
      n = split.length - 1;
    }
    return String.join("/", Arrays.copyOfRange(split, 0, n));
  }
}
