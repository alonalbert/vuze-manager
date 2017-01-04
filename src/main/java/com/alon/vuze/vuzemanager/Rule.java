package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.utils.WildcardMatcher;
import com.google.gson.annotations.Expose;
import org.json.simple.JSONObject;

public class Rule {

  private static final String CATEGORY = "category";
  private static final String ACTION = "action";
  private static final String ARG = "arg";

  public enum Action {
    FORCE_SEED("vuzeManager.rules.action.forceSeed"),
    CATEGORY_AUTO_DELETE("vuzeManager.rules.action.categoryAutoDelete"),
    WATCHED_AUTO_DELETE("vuzeManager.rules.action.watchedAutoDelete"),
    AUTO_DESTINATION("vuzeManager.rules.action.autoDestination");

    private final String messageKey;

    Action(String messageKey) {
      this.messageKey = messageKey;
    }

    public String getMessageKey() {
      return messageKey;
    }
  }

  @Expose
  private final String qualifier;
  private final WildcardMatcher matcher;
  @Expose
  private final Action action;
  @Expose
  private final String arg;

  public Rule(String qualifier, Action action, String arg) {
    this.qualifier = qualifier;
    this.action = action;
    this.arg = arg;
    matcher = new WildcardMatcher(qualifier);
  }

  String getCategory() {
    return qualifier;
  }

  WildcardMatcher getMatcher() {
    return matcher;
  }

  Action getAction() {
    return action;
  }

  String getArg() {
    return arg;
  }

  int getArgAsInt() {
    return Integer.parseInt(arg);
  }

  JSONObject toJson() {
    final JSONObject json= new JSONObject();
    json.put(CATEGORY, qualifier);
    json.put(ACTION, action.toString());
    json.put(ARG, arg);
    return json;
  }

  static Rule fromJson(JSONObject json) {
    return new Rule(
        (String) json.get(CATEGORY),
        Action.valueOf((String) json.get(ACTION)),
        (String) json.get(ARG));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Rule that = (Rule) o;
    //noinspection SimplifiableIfStatement
    if (!qualifier.equals(that.qualifier)) {
      return false;
    }
    return action == that.action;
  }

  @Override
  public String toString() {
    return "Rule{"
        + "category='" + qualifier + '\''
        + ", action=" + action
        + ", arg=" + arg
        + '}';
  }
}
