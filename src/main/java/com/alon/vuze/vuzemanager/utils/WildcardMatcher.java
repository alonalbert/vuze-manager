package com.alon.vuze.vuzemanager.utils;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class WildcardMatcher {

  private final Pattern pattern;

  @SuppressWarnings("ReplaceAllDot")
  public WildcardMatcher(String wildcard) {
    final String regex = wildcard
        .replaceAll(".", "[$0]")
        .replace("[*]", ".*")
        .replace("[#]", "\\d")
        .replace("[?]", ".");
    pattern = Pattern.compile(regex, CASE_INSENSITIVE);
  }

  public boolean matches(CharSequence input) {
    return input != null && pattern.matcher(input).matches();
  }
}
