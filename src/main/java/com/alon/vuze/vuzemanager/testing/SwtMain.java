package com.alon.vuze.vuzemanager.testing;

import com.alon.vuze.vuzemanager.Config;
import com.alon.vuze.vuzemanager.logger.DebugLogger;
import com.alon.vuze.vuzemanager.resources.DebugMessages;
import java.io.IOException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class SwtMain {

  public static void main(String[] args) throws IOException {
    final Config config = new Config("out", new DebugLogger());
    final DebugMessages messages = new DebugMessages();
    final Display display = new Display();
    final Shell shell = new Shell();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}
