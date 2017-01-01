package com.alon.vuze.vuzemanager;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

class GraphicUtils {
    static void centerShellandOpen(Display display, Shell shell){
        shell.pack();
        final Monitor primary = display.getPrimaryMonitor ();
        final Rectangle bounds = primary.getBounds ();
        final Rectangle rect = shell.getBounds ();
        shell.setLocation (
            bounds.x + (bounds.width - rect.width) / 2,
            bounds.y +(bounds.height - rect.height) / 2);
        shell.open();
    }
}
