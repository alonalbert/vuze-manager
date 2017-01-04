package com.alon.vuze.vuzemanager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

public interface Annotations {
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface PluginDirectory {}
}
