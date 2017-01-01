package com.alon.vuze.vuzemanager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

class MainView implements UISWTViewEventListener {

  static final String VIEWID = "VuzeManagerView";
  private final PluginInterface pluginInterface;
  private final Config config;

  MainView(PluginInterface pluginInterface, Config config) {
    this.pluginInterface = pluginInterface;
    this.config = config;
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
    final Display display = parent.getDisplay();

    final Label titleLabel = new Label(parent, SWT.BORDER);
    Messages.setLanguageText(titleLabel, "Views.plugins.VuzeManagerView.title");
    titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
    titleLabel.setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
    titleLabel.setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
    final FontData[] titleFontData = titleLabel.getFont().getFontData();
    titleFontData[0].setStyle(SWT.BOLD);
    titleFontData[0].setHeight((int)(titleFontData[0].getHeight() * 1.2));
    titleLabel.setFont(new Font(display, titleFontData));

    final CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER);
    final GridData gridData = new GridData(GridData.FILL_BOTH);
    tabFolder.setLayoutData(gridData);
    tabFolder.setMinimumCharacters(75);
    tabFolder.setSelectionBackground(new Color[] {
            display.getSystemColor(SWT.COLOR_LIST_BACKGROUND),
            display.getSystemColor(SWT.COLOR_LIST_BACKGROUND),
            tabFolder.getBackground()},
        new int[] {10, 90}, true);
    tabFolder.setSelectionForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
    tabFolder.setSimple(false);

    final CTabItem tabCategories = new CTabItem(tabFolder, SWT.NULL);
    Messages.setLanguageText(tabCategories, "vuzeManager.tab.categories");
    tabCategories.setControl(new CatagoriesView(tabFolder, pluginInterface, config));

    final CTabItem tabPlex = new CTabItem(tabFolder, SWT.NULL);
    Messages.setLanguageText(tabPlex, "vuzeManager.tab.plex");
    // todo: save to config
    tabFolder.setSelection(0);
  }

  private void delete() {
    ImageRepository.unLoadImages();
  }

}
