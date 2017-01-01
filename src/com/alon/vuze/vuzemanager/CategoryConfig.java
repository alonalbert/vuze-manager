package com.alon.vuze.vuzemanager;

public class CategoryConfig {
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

  public Action getAction() {
    return action;
  }

  int getDays() {
    return days;
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
}
