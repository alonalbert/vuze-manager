package com.alon.vuze.vuzemanager;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class GraphicUtils {
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
    
    public static void centerShellRelativeToandOpen(final Shell shell, final Control control){
        shell.pack();
        final Rectangle bounds = control.getBounds();
        final Point shellSize = shell.getSize();
        shell.setLocation(
                bounds.x + (bounds.width / 2) - shellSize.x / 2,
                bounds.y + (bounds.height / 2) - shellSize.y / 2);
        shell.open();
    } 
}
