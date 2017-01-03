package com.alon.vuze.vuzemanager.resources;

import org.eclipse.swt.widgets.Widget;

public interface Messages {
  void setLanguageText(Widget widget, String key);

  void setLanguageTooltip(Widget widget, String key);

  String getString(String key);
}
