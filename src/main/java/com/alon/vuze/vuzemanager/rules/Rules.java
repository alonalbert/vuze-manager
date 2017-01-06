package com.alon.vuze.vuzemanager.rules;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.rules.Rule.Action;
import com.google.common.reflect.TypeToken;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

@Singleton
public class Rules implements Collection<Rule> {
  private static final String RULES = "rulesView.rules";

  private final Config config;

  private final Set<Rule> rules;

  @Inject
  public Rules(Config config) {
    this.config = config;
    rules = config.getTyped(RULES, new TypeToken<HashSet<Rule>>() {}.getType(), new HashSet<>());
  }

  public boolean add(Rule rule) {
    return rules.add(rule);
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Rule> c) {
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  @Override
  public void clear() {

  }

  public void remove(Rule rule) {
    rules.remove(rule);
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

  public long getCount(Action action) {
    return rules.stream().filter(
        rule -> rule.getAction() == action)
        .count();
  }

  @Override
  public int size() {
    return rules.size();
  }

  @Override
  public boolean isEmpty() {
    return rules.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return rules.contains(o);
  }

  @Override
  public Iterator<Rule> iterator() {
    return rules.iterator();
  }

  @Override
  public Object[] toArray() {
    return rules.toArray();
  }

  @SuppressWarnings("SuspiciousToArrayCall")
  @Override
  public <T> T[] toArray(T[] a) {
    return rules.toArray(a);
  }

  @Override
  public void forEach(Consumer<? super Rule> action) {
    rules.forEach(action);
  }

  @Override
  public Spliterator<Rule> spliterator() {
    return rules.spliterator();
  }
}
