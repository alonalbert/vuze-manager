package com.alon.vuze.vuzemanager.rules;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.rules.Rule.Action;
import com.google.common.reflect.TypeToken;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class Rules {
  private static final String RULES = "rulesView.rules";

  private final Config config;

  private final Set<Rule> rules;

  @Inject
  public Rules(Config config) {
    this.config = config;
    rules = config.getTyped(RULES, new TypeToken<HashSet<Rule>>() {}.getType(), new HashSet<>());
  }

  public Collection<Rule> getRules() {
    return Collections.unmodifiableCollection(rules);
  }

  public void add(Rule rule) {
    rules.add(rule);
  }

  public void update(Rule oldRule, Rule newRule) {
    rules.remove(oldRule);
    rules.add(newRule);
  }

  public void setConfig() {
    config.set(RULES, rules);
  }

  public Rule findFirst(Action action, String text) {
    return rules.stream().filter(
        rule -> rule.getAction() == action && rule.getMatcher().matches(text))
        .findFirst().orElse(null);
  }

}
