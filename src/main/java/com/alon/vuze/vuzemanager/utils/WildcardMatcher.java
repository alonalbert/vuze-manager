package com.alon.vuze.vuzemanager.utils;

import java.util.regex.Pattern;

public class WildcardMatcher {

  private final Pattern pattern;

  @SuppressWarnings("ReplaceAllDot")
  public WildcardMatcher(String wildcard) {
    final String regex = wildcard
        .replaceAll(".", "[$0]")
        .replace("[*]", ".*")
        .replace("[?]", ".");
    pattern = Pattern.compile(regex);
  }

  public boolean matches(CharSequence input) {
    return pattern.matcher(input).matches();
  }
}
