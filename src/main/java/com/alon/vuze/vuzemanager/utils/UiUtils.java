package com.alon.vuze.vuzemanager.utils;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UiUtils {
  public static void selectDirectory(Shell shell, String title, String message, Text target) {
    final DirectoryDialog dlg = new DirectoryDialog(shell);
    dlg.setFilterPath(target.getText());

    dlg.setText(title);
    dlg.setMessage(message);
    final String dir = dlg.open();
    if (dir != null) {
      target.setText(dir);
    }
  }
}
