package com.alon.vuze.vuzemanager.utils;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class WordUtils {
  private static final Set<String> LOWERCASE_WORDS = ImmutableSet.of(
      "a",
      "in",
      "of",
      "the"
  );

  public static String titleCase(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    int strLen = str.length();
    StringBuilder buffer = new StringBuilder(strLen);
    boolean capitalizeNext = true;
    for (int i = 0; i < strLen; i++) {
      char ch = str.charAt(i);

      if (Character.isWhitespace(ch)) {
        buffer.append(ch);
        capitalizeNext = true;
      } else if (capitalizeNext) {
        final String word = getWordAtPos(str, i);
        final char c;
        if (i > 0 && LOWERCASE_WORDS.contains(word)) {
          c = ch;
        } else {
          c = Character.toTitleCase(ch);
        }
        buffer.append(c);
        capitalizeNext = false;
      } else {
        buffer.append(ch);
      }
    }
    return buffer.toString();
  }

  private static String getWordAtPos(String str, int i) {
    return str.substring(i, getWordEnd(str, i));
  }

  private static int getWordEnd(String str, int i) {
    while (i < str.length()) {
      if (Character.isWhitespace(str.charAt(i))) {
        return i;
      }
      i++;
    }
    return i;
  }

}

