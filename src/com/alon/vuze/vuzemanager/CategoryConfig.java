package com.alon.vuze.vuzemanager;

import org.json.simple.JSONObject;

class CategoryConfig {

  private static final String CATEGORY = "category";
  private static final String ACTION = "action";
  private static final String DAYS = "days";

  enum Action {
    FORCE_SEED("vuzeManager.categories.action.forceSeed"),
    AUTO_DELETE("vuzeManager.categories.action.autoDelete"),;

    private final String messageKey;

    Action(String messageKey) {
      this.messageKey = messageKey;
    }

    public String getMessageKey() {
      return messageKey;
    }
  }

  private final String category;
  private final Action action;
  private final int days;

  CategoryConfig(String category, Action action, int days) {
    this.category = category;
    this.action = action;
    this.days = days;
  }

  String getCategory() {
    return category;
  }

  Action getAction() {
    return action;
  }

  int getDays() {
    return days;
  }

  JSONObject toJson() {
    final JSONObject json= new JSONObject();
    json.put(CATEGORY, category);
    json.put(ACTION, action.toString());
    json.put(DAYS, days);
    return json;
  }

  static CategoryConfig fromJson(JSONObject json) {
    return new CategoryConfig(
        (String) json.get(CATEGORY),
        Action.valueOf((String) json.get(ACTION)),
        (int) (long) json.get(DAYS));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final CategoryConfig that = (CategoryConfig) o;
    //noinspection SimplifiableIfStatement
    if (!category.equals(that.category)) {
      return false;
    }
    return action == that.action;
  }

  @Override
  public int hashCode() {
    int result = category.hashCode();
    result = 31 * result + action.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CategoryConfig{");
    sb.append("category='").append(category).append('\'');
    sb.append(", action=").append(action);
    sb.append(", days=").append(days);
    sb.append('}');
    return sb.toString();
  }
}
