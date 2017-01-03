package com.alon.vuze.vuzemanager.categories;

import com.alon.vuze.vuzemanager.utils.Wildcard;
import org.json.simple.JSONObject;

public class Rule {

  private static final String CATEGORY = "category";
  private static final String ACTION = "action";
  private static final String ARG = "arg";

  public enum Action {
    FORCE_SEED("vuzeManager.categories.action.forceSeed"),
    CATEGORY_AUTO_DELETE("vuzeManager.categories.action.categoryAutoDelete"),
    WATCHED_AUTO_DELETE("vuzeManager.categories.action.watchedAutoDelete"),
    AUTO_DESTINATION("vuzeManager.categories.action.audoDestination");

    private final String messageKey;

    Action(String messageKey) {
      this.messageKey = messageKey;
    }

    public String getMessageKey() {
      return messageKey;
    }
  }

  private final String category;
  private final Wildcard wildcard;
  private final Action action;
  private final String arg;

  public Rule(String category, Action action, String arg) {
    this.category = category;
    this.action = action;
    this.arg = arg;
    wildcard = new Wildcard(category);
  }

  String getCategory() {
    return category;
  }

  Wildcard getWildcard() {
    return wildcard;
  }

  Action getAction() {
    return action;
  }

  public String getArg() {
    return arg;
  }

  public int getArgAsInt() {
    return Integer.parseInt(arg);
  }

  public JSONObject toJson() {
    final JSONObject json= new JSONObject();
    json.put(CATEGORY, category);
    json.put(ACTION, action.toString());
    json.put(ARG, arg);
    return json;
  }

  public static Rule fromJson(JSONObject json) {
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
    if (!category.equals(that.category)) {
      return false;
    }
    return action == that.action;
  }

  @Override
  public String toString() {
    return "Rule{"
        + "category='" + category + '\''
        + ", action=" + action
        + ", arg=" + arg
        + '}';
  }
}
