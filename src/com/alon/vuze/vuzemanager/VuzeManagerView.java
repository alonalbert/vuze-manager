package com.alon.vuze.vuzemanager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

public class VuzeManagerView implements UISWTViewEventListener {

  static final String VIEWID = "VuzeManagerView";
  private final PluginInterface pluginInterface;
  private Display display;
  private Shell shell;

  private CTabItem tabAutoForceSeed;
  private CTabItem tabCategoryAutoDelete;
  private CTabItem tabPlexAutoDelete;

  VuzeManagerView(PluginInterface pluginInterface) {
    this.pluginInterface = pluginInterface;
  }

  @Override
  public boolean eventOccurred(UISWTViewEvent event) {
    switch (event.getType()) {
      case UISWTViewEvent.TYPE_CREATE:
        break;

      case UISWTViewEvent.TYPE_INITIALIZE:
        initialize((Composite) event.getData());
        break;

      case UISWTViewEvent.TYPE_REFRESH:
        break;

      case UISWTViewEvent.TYPE_DESTROY:
        delete();
        break;

      case UISWTViewEvent.TYPE_DATASOURCE_CHANGED:
        break;

      case UISWTViewEvent.TYPE_FOCUSGAINED:
        break;

      case UISWTViewEvent.TYPE_FOCUSLOST:
        break;

      case UISWTViewEvent.TYPE_LANGUAGEUPDATE:
        break;
    }
    return true;
  }

  private void initialize(Composite parent) {
    this.display = parent.getDisplay();
    this.shell = parent.getShell();

    final Composite titleComposite = setupComposite(
        parent,
        SWT.BORDER,
        setupGridLayout(-1, -1, -1, 3, 0),
        GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);

    titleComposite.setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
    titleComposite.setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));

    final Label titleLabel = new Label(titleComposite, SWT.NULL);
    titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
    titleLabel.setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
    titleLabel.setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
    final FontData[] titleFontData = titleLabel.getFont().getFontData();
    titleFontData[0].setStyle(SWT.BOLD);
    titleFontData[0].setHeight((int)(titleFontData[0].getHeight() * 1.2));
    titleLabel.setFont(new Font(display, titleFontData));
    Messages.setLanguageText(titleLabel, "Views.plugins.VuzeManagerView.title");

    final CTabFolder tabFolder = new CTabFolder(parent, SWT.FLAT);
    tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
    tabFolder.setMinimumCharacters(75);
    tabFolder.setSelectionBackground(new Color[] {
        display.getSystemColor(SWT.COLOR_LIST_BACKGROUND),
        display.getSystemColor(SWT.COLOR_LIST_BACKGROUND),
        tabFolder.getBackground()},
        new int[] {10, 90}, true);
    tabFolder.setSelectionForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));

    tabAutoForceSeed = new CTabItem(tabFolder, SWT.NULL);
    Messages.setLanguageText(tabAutoForceSeed, "VuzeManager.Tab.AutoForceSeed");
    tabCategoryAutoDelete = new CTabItem(tabFolder, SWT.NULL);
    Messages.setLanguageText(tabCategoryAutoDelete, "VuzeManager.Tab.CategoryAutoDelete");
    tabPlexAutoDelete = new CTabItem(tabFolder, SWT.NULL);
    Messages.setLanguageText(tabPlexAutoDelete, "VuzeManager.Tab.PlexAutoDelete");
    // todo: save to config
    tabFolder.setSelection(0);


  }

  private void delete() {

  }

  private Composite setupComposite(Composite parent, int style, GridLayout layout, int gridStyle) {
    Composite cmp = new Composite(parent, style);
    cmp.setLayout(layout);
    cmp.setLayoutData(gridStyle == -1 ? new GridData() : new GridData(gridStyle));
    return cmp;
  }

  private GridLayout setupGridLayout(
      int numColumns,
      int horizontalSpacing,
      int verticalSpacing,
      int marginHeight, int marginWidth) {
    final GridLayout layout = new GridLayout();
    if(numColumns != -1) {
      layout.numColumns = numColumns;
    }
    if(horizontalSpacing != -1) {
      layout.horizontalSpacing = horizontalSpacing;
    }
    if(verticalSpacing != -1) {
      layout.verticalSpacing = verticalSpacing;
    }
    if(marginHeight != -1) {
      layout.marginHeight = marginHeight;
    }
    if(marginWidth != -1) {
      layout.marginWidth = marginWidth;
    }
    return layout;
  }
}
