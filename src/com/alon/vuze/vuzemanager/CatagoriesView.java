package com.alon.vuze.vuzemanager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.ui.swt.Messages;

import java.util.Set;

import static com.alon.vuze.vuzemanager.ImageRepository.ImageResource.ADD;
import static com.alon.vuze.vuzemanager.ImageRepository.ImageResource.REMOVE;

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
      final Shell shell = new Shell();
      shell.setLayout(new GridLayout());
      Messages.setLanguageText(shell, "vuzeManager.categories.add.popup.title");
      shell.setImage(ImageRepository.getImage(display, ADD));

      final Composite body = new Composite(shell, SWT.BORDER);
      body.setLayout(new GridLayout(2, false));
      body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      final Label categoryLabel = new Label(body, SWT.NULL);
      Messages.setLanguageText(categoryLabel, "vuzeManager.categories.add.popup.category");
      final GridData labelLayout = new GridData();
      labelLayout.widthHint = 200;
      categoryLabel.setLayoutData(labelLayout);

      final Text categoryEdit = new Text(body, SWT.SINGLE | SWT.BORDER);
      final GridData valueLayout = new GridData(GridData.FILL_HORIZONTAL);
      valueLayout.widthHint = 250;
      categoryEdit.setLayoutData(valueLayout);

      final Label actionLabel = new Label(body, SWT.NULL);
      Messages.setLanguageText(actionLabel, "vuzeManager.categories.add.popup.action");
      actionLabel.setLayoutData(labelLayout);

      final Combo acionCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
      acionCombo.setLayoutData(valueLayout);
      for (CategoryConfig.Action action : CategoryConfig.Action.values()) {
        acionCombo.add(MessageText.getString(action.getMessageKey()));
      }
      acionCombo.setText(acionCombo.getItem(0));

      final Label daysLabel = new Label(body, SWT.NULL);
      Messages.setLanguageText(daysLabel, "vuzeManager.categories.add.popup.days");
      daysLabel.setLayoutData(labelLayout);

      final Spinner daysSpinner = new Spinner(body, SWT.SINGLE | SWT.BORDER);
      daysSpinner.setLayoutData(valueLayout);
      daysSpinner.setSelection(7);
      daysSpinner.setMinimum(1);

      final Composite buttons = new Composite(shell, SWT.NULL);
      buttons.setLayout(new GridLayout(2, false));
      buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      final Button cancel = new Button(buttons, SWT.PUSH);
      cancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
      Messages.setLanguageText(cancel, "vuzeManager.categories.add.popup.cancel");
      cancel.addListener(SWT.Selection, event -> shell.dispose());

      final Button ok = new Button(buttons, SWT.PUSH);
      ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
      Messages.setLanguageText(ok, "vuzeManager.categories.add.popup.ok");
      ok.addListener(SWT.Selection, event -> {
        final String category = categoryEdit.getText();
        if(!category.isEmpty()) {
          final Set<CategoryConfig> categories = config.getCategories();
          final CategoryConfig.Action action = CategoryConfig.Action.values()[acionCombo.getSelectionIndex()];
          final CategoryConfig categoryConfig = new CategoryConfig(category, action, daysSpinner.getSelection());
          if (categories.contains(categoryConfig)) {
            categories.remove(categoryConfig);
          }
          categories.add(categoryConfig);
          shell.dispose();
          populateTable(categories);
        }
      });

      //open shell
      GraphicUtils.centerShellandOpen(display, shell);


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

    final TableColumn type = new TableColumn(table, SWT.NULL);
    Messages.setLanguageText(type, "vuzeManager.categories.column.type");
    type.setWidth(200);

    final TableColumn days = new TableColumn(table, SWT.NULL);
    Messages.setLanguageText(days, "vuzeManager.categories.column.days");
    days.setWidth(200);
  }

  private void populateTable(Set<CategoryConfig> categories) {
    try{
      if(table != null && !table.isDisposed()){
        table.removeAll();
        for (CategoryConfig category : categories) {
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
