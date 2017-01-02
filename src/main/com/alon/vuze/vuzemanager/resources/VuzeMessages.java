package com.alon.vuze.vuzemanager.resources;

import org.eclipse.swt.widgets.Widget;
import org.gudy.azureus2.core3.internat.MessageText;

import javax.inject.Singleton;

@Singleton
public class VuzeMessages implements Messages {

  @Override
  public void setLanguageText(Widget widget, String key) {
    org.gudy.azureus2.ui.swt.Messages.setLanguageText(widget, key);
  }

  @Override
  public void setLanguageTooltip(Widget widget, String key) {
    org.gudy.azureus2.ui.swt.Messages.setLanguageTooltip(widget, key);
  }

  @Override
  public String getString(String key) {
    return MessageText.getString(key);
  }
}
