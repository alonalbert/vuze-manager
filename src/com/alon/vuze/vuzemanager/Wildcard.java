package com.alon.vuze.vuzemanager;

import java.util.regex.Pattern;

public class Wildcard {

  private final Pattern pattern;

  public Wildcard(String wildcard) {
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
