package com.alon.vuze.vuzemanager;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.ui.swt.KeyBindings;
import org.gudy.azureus2.ui.swt.components.DoubleBufferedLabel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class DebugMessages implements Messages {
  private final Properties properties = new Properties();
  DebugMessages() throws IOException {
    final ClassLoader classLoader = DebugMessages.class.getClassLoader();
    final String base = VuzeManagerPlugin.class.getPackage().getName().replace(".", "/");
    final String path = base + "/messages.properties";
    try (InputStream in = classLoader.getResourceAsStream(path)) {
      properties.load(in);
    }
  }

  @Override
  public void setLanguageText(Widget widget, String key) {
    final String message = getString(key);
    if (widget instanceof MenuItem) {
      final MenuItem menuItem = ((MenuItem) widget);
      boolean indent = (menuItem.getData("IndentItem") != null);

      menuItem.setText(indent ? "  " + message : message);

      if (menuItem.getAccelerator() != 0) // opt-in only for now; remove this conditional check to allow accelerators for arbitrary MenuItem objects
        KeyBindings.setAccelerator(menuItem, (String) menuItem.getData()); // update keybinding
    } else if (widget instanceof TableColumn) {
      TableColumn tc = ((TableColumn) widget);
      tc.setText(message);
    } else if (widget instanceof Label)
      // Disable Mnemonic when & is before a space.  Otherwise, it's most
      // likely meant to be a Mnemonic
      ((Label) widget).setText(message.replaceAll("& ", "&& "));
    else if (widget instanceof CLabel)
      ((CLabel) widget).setText(message.replaceAll("& ", "&& "));
    else if (widget instanceof Group)
      ((Group) widget).setText(message);
    else if (widget instanceof Button)
      ((Button) widget).setText(message);
    else if (widget instanceof CTabItem)
      ((CTabItem) widget).setText(message);
    else if (widget instanceof TabItem)
      ((TabItem) widget).setText(message);
    else if (widget instanceof TreeItem)
      ((TreeItem) widget).setText(message);
    else if (widget instanceof Shell)
      ((Shell) widget).setText(message);
    else if (widget instanceof ToolItem)
      ((ToolItem) widget).setText(message);
    else if (widget instanceof Text)
      ((Text) widget).setText(message);
    else if (widget instanceof TreeColumn)
      ((TreeColumn) widget).setText(message);
    else if (widget instanceof DoubleBufferedLabel)
      ((DoubleBufferedLabel) widget).setText(message);
    else //noinspection StatementWithEmptyBody
      if (widget instanceof Canvas)
      ; // get a few of these
    else {
      Debug.out("No cast for " + widget.getClass().getName());
    }
  }

  @Override
  public void setLanguageTooltip(Widget widget, String key) {
    if (widget == null || widget.isDisposed()) {
      return;
    }
    if (widget.getData() != null) {
      String sToolTip = getString(key);
      if (widget instanceof CLabel)
        ((CLabel) widget).setToolTipText(sToolTip);
      else if (widget instanceof Label)
        ((Label) widget).setToolTipText(sToolTip);
      else if (widget instanceof Text)
        ((Text) widget).setToolTipText(sToolTip);
      else if (widget instanceof Canvas)
        ((Canvas) widget).setToolTipText(sToolTip);
      else
        System.out.println("No cast for " + widget.getClass().getName());
    }
  }

  @Override
  public String getString(String key) {
    return properties.getProperty(key, key);
  }
}
