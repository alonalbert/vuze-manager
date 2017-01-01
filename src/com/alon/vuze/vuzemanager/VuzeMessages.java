package com.alon.vuze.vuzemanager;

import org.eclipse.swt.widgets.Widget;
import org.gudy.azureus2.core3.internat.MessageText;

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
