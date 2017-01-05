package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.RuleDialog;
import com.alon.vuze.vuzemanager.rules.Rules;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.ADD;
import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.REMOVE;
import static org.gudy.azureus2.ui.swt.Utils.getDisplay;

class MainView implements UISWTViewEventListener {

  private static final String QUALIFIER_WIDTH = "rulesView.qualifierWidth";
  private static final String ACTION_WIDTH = "rulesView.actionWidth";
  private static final String ARG_WIDTH = "rulesView.argWidth";

  @SuppressWarnings("unused")
  @Inject
  private Config config;

  @SuppressWarnings("unused")
  @Inject
  private Logger logger;

  @SuppressWarnings("unused")
  @Inject
  private Messages messages;

  @SuppressWarnings("unused")
  @Inject
  private ImageRepository imageRepository;

  @Inject
  private VuzeManagerPlugin.Factory factory;

  @Inject
  private Rules rules;

  private Table table;
  private ToolItem remove;
  private TableColumn qualifier;
  private TableColumn action;
  private TableColumn arg;

  @Inject
  public MainView() {
  }

  @Override
  public boolean eventOccurred(UISWTViewEvent event) {
    switch (event.getType()) {
      case UISWTViewEvent.TYPE_INITIALIZE:
        initialize((Composite) event.getData());
        break;

      case UISWTViewEvent.TYPE_DESTROY:
        delete();
        break;
    }
    return true;
  }

  private void initialize(Composite root) {
    final Display display = getDisplay();

    root.setLayout(new GridLayout());
    root.setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label label = new Label(root, SWT.NULL);
    messages.setLanguageText(label, "vuzeManager.rules.label");

    final ToolBar toolBar = new ToolBar(root, SWT.BORDER | SWT.FLAT);

    final ToolItem add = new ToolItem(toolBar, SWT.PUSH);
    add.setImage(imageRepository.getImage(display, ADD));
    messages.setLanguageTooltip(add, "vuzeManager.rules.add");
    add.addListener(SWT.Selection, e -> handleAddItem());

    table = new Table(root,
        SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    remove = new ToolItem(toolBar, SWT.PUSH);
    remove.setImage(imageRepository.getImage(display, REMOVE));
    messages.setLanguageTooltip(remove, "vuzeManager.rules.remove");
    remove.setEnabled(false);
    remove.addListener(SWT.Selection, e -> handleRemoveItem());

    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);

    final ControlListener columnResizeListener = new ControlListener() {
      @Override
      public void controlMoved(ControlEvent e) {

      }

      @Override
      public void controlResized(ControlEvent e) {
        final TableColumn source = (TableColumn) e.getSource();
        System.out.printf("Width of %s is %d\n", source.getData(), source.getWidth());
      }
    };

    qualifier = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(qualifier, "vuzeManager.rules.column.name");
    qualifier.setWidth(200);
    qualifier.addControlListener(columnResizeListener);
    setColumnWidth(qualifier, QUALIFIER_WIDTH);

    action = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(action, "vuzeManager.rules.column.action");
    action.setWidth(250);
    action.addControlListener(columnResizeListener);
    setColumnWidth(action, ACTION_WIDTH);

    arg = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(arg, "vuzeManager.rules.column.arg");
    arg.setWidth(600);
    arg.addControlListener(columnResizeListener);
    setColumnWidth(arg, ARG_WIDTH);

    //listener to deselect if outside an item
    table.addListener(SWT.Selection, event -> remove.setEnabled(table.getSelection().length >= 1));
    table.addListener (SWT.MouseDoubleClick, event -> handleItemDoubleClick());

    populateTable();
  }

  private void setColumnWidth(TableColumn column, String name) {
    final int width = config.get(name, -1);
    if (width != -1) {
      column.setWidth(width);
    }
  }

  private void delete() {
    imageRepository.unLoadImages();
    rules.setConfig();
    config.set(QUALIFIER_WIDTH, qualifier.getWidth());
    config.set(ACTION_WIDTH, action.getWidth());
    config.set(ARG_WIDTH, arg.getWidth());
    config.save();
  }

  private void handleItemDoubleClick() {
    final TableItem[] items = table.getSelection();
    if (items.length == 1) {
      final Rule rule = (Rule) items[0].getData();
      final RuleDialog ruleDialog = factory.createRunDialog(
          getDisplay(),
          newRule -> updateRule(rule, newRule));
      ruleDialog.initializeAndOpen(rule);
    }
  }

  private void handleAddItem() {
    final RuleDialog ruleDialog = factory.createRunDialog(
        getDisplay(),
        this::addRule);
    ruleDialog.initializeAndOpen(null);
  }

  private void addRule(Rule rule) {
    rules.add(rule);
    populateTable();
  }

  private void updateRule(Rule oldRule, Rule newRule) {
    rules.update(oldRule, newRule);
    populateTable();
  }

  private void handleRemoveItem() {
    final TableItem[] items = table.getSelection();
    if(items.length == 1){
      final Rule rule = (Rule) items[0].getData();
      rules.getRules().remove(rule);
      final int oldSelectedIndex = table.getSelectionIndex();
      populateTable();
      final int itemCount = table.getItemCount();
      if (itemCount == 0) {
        remove.setEnabled(false);
      } else {
        if (oldSelectedIndex < itemCount) {
          table.setSelection(oldSelectedIndex);
        } else {
          table.setSelection(itemCount - 1);
        }
      }
    }
  }

  private void populateTable() {
    try {
      if (table != null && !table.isDisposed()) {
        table.removeAll();
        final List<Rule> sorted = new ArrayList<>(rules.getRules());
        sorted.sort(Comparator
            .comparing(Rule::getAction)
            .thenComparing(Rule::getCategory));
        sorted.forEach(this::addCategoryToTable);
      }
    } catch (Exception e) {
      logger.log(e, "Error populating table");
    }
  }

  private void addCategoryToTable(Rule rule) {
    final TableItem item = new TableItem(table, SWT.NULL);
    item.setData(rule);
    item.setText(0, rule.getCategory());
    final Rule.Action action = rule.getAction();
    item.setText(1, MessageText.getString(action.getMessageKey()));
    switch (action) {
      case FORCE_SEED:
        break;
      case CATEGORY_AUTO_DELETE:
      case WATCHED_AUTO_DELETE:
      case AUTO_DESTINATION:
        item.setText(2, String.valueOf(rule.getArg()));
        break;
    }
  }

}
