package com.alon.vuze.vuzemanager;

import static com.alon.vuze.vuzemanager.ImageRepository.ImageResource.ADD;
import static com.alon.vuze.vuzemanager.ImageRepository.ImageResource.REMOVE;

import java.util.ArrayList;
import java.util.Comparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.ui.swt.Messages;

class CatagoriesView extends Composite {
  private final PluginInterface pluginInterface;
  private final Config config;
  private final Table table;

  CatagoriesView(Composite parent, PluginInterface pluginInterface, Config config) {
    super(parent, SWT.BORDER);
    this.pluginInterface = pluginInterface;
    this.config = config;

    final Display display = getDisplay();

    setLayout(new GridLayout());
    setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label label = new Label(this, SWT.NULL);
    Messages.setLanguageText(label, "vuzeManager.categories.label");


    final ToolBar toolBar = new ToolBar(this, SWT.BORDER | SWT.FLAT);

    final ToolItem add = new ToolItem(toolBar, SWT.PUSH);
    add.setImage(ImageRepository.getImage(display, ADD));
    Messages.setLanguageTooltip(add, "vuzeManager.categories.add");
    add.addListener(SWT.Selection, e -> {
      final CategoryDialog categoryDialog = new CategoryDialog(display, config);
      categoryDialog.setOnOkListener(categoryConfig -> populateTable());
      categoryDialog.open();
    });

    final ToolItem remove = new ToolItem(toolBar, SWT.PUSH);
    remove.setImage(ImageRepository.getImage(display, REMOVE));
    Messages.setLanguageTooltip(remove, "vuzeManager.categories.remove");
    remove.setEnabled(false);

    table = new Table(this, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);

    // TODO: 12/31/16 Persist comulmn widths in config
    final TableColumn name = new TableColumn(table, SWT.NULL);
    Messages.setLanguageText(name, "vuzeManager.categories.column.name");
    name.setWidth(200);

    final TableColumn action = new TableColumn(table, SWT.NULL);
    Messages.setLanguageText(action, "vuzeManager.categories.column.action");
    action.setWidth(250);

    final TableColumn days = new TableColumn(table, SWT.NULL);
    Messages.setLanguageText(days, "vuzeManager.categories.column.days");
    days.setWidth(200);

    populateTable();
  }

  private void populateTable() {
    try{
      if(table != null && !table.isDisposed()){
        table.removeAll();
        final ArrayList<CategoryConfig> sorted = new ArrayList<>(config.getCategories());
        sorted.sort(Comparator.comparing(CategoryConfig::getCategory));
        for (CategoryConfig category : sorted) {
          addCategoryToTable(category);
        }
      }
    }catch(Exception e){
      // TODO: 12/31/16 handle
    }
  }

  private void addCategoryToTable(CategoryConfig categoryConfig){
    final TableItem item = new TableItem(table,SWT.NULL);
    item.setText(0, categoryConfig.getCategory());
    item.setText(1, MessageText.getString(categoryConfig.getAction().getMessageKey()));
    item.setText(2, String.valueOf(categoryConfig.getDays()));
  }

}
