package com.alon.vuze.vuzemanager.ui;

import com.alon.vuze.vuzemanager.CategoryAutoDeleter;
import com.alon.vuze.vuzemanager.PlexAutoDeleter;
import com.alon.vuze.vuzemanager.ViewFactory;
import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.alon.vuze.vuzemanager.rules.Rules;
import com.alon.vuze.vuzemanager.utils.CompositeBuilder;
import com.alon.vuze.vuzemanager.utils.GridDataBuilder;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.ADD;
import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.REMOVE;

public class RulesSection extends Composite implements ConfigSection {
  public static final String FAKE_DELETE = "fakeDelete";

  private static final String QUALIFIER_WIDTH = "rulesView.qualifierWidth";
  private static final String ACTION_WIDTH = "rulesView.actionWidth";
  private static final String ARG_WIDTH = "rulesView.argWidth";
  private static final int INITIAL_WIDTH = 600;
  private static final int INITIAL_HEIGHT = 400;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  @Inject
  private Config config;

  @Inject
  private Logger logger;

  @Inject
  private ImageRepository imageRepository;

  @Inject
  private ViewFactory factory;

  @Inject
  private Messages messages;

  @Inject
  private Rules rules;

  @Inject
  private PlexAutoDeleter plexAutoDeleter;

  @Inject
  private CategoryAutoDeleter categoryAutoDeleter;

  private Table table;
  private Button remove;
  private Button deleteCompleted;
  private Button deleteWatched;
  private TableColumn qualifier;
  private TableColumn action;
  private TableColumn arg;
  private Button fakeDelete;
  private ConfigSection parentSection;

  @AssistedInject
  public RulesSection(@Assisted Composite parent) {
    super(parent, SWT.NONE);

    setLayout(new GridLayout());
    setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, true, true).build());
    setSize(new Point(INITIAL_WIDTH, INITIAL_HEIGHT));
  }

  @Override
  public void initialize(ConfigSection parent) {
    parentSection = parent;
    final Display display = getDisplay();

    final Composite rulesSection = new CompositeBuilder(this)
        .setGridData(new GridDataBuilder(GridData.FILL, GridData.FILL, true, true))
        .build();
    final Label label = new Label(rulesSection, SWT.NULL);
    messages.setLanguageText(label, "vuzeManager.config.rules.label");

    final Composite toolBar = new CompositeBuilder(rulesSection, SWT.NONE, 5).build();

    final Button add = new Button(toolBar, SWT.PUSH);
    add.setImage(imageRepository.getImage(display, ADD));
    messages.setLanguageText(add, "vuzeManager.config.rules.add");
    messages.setLanguageTooltip(add, "vuzeManager.config.rules.add");
    add.addListener(SWT.Selection, e -> handleAddItem());

    remove = new Button(toolBar, SWT.PUSH);
    remove.setImage(imageRepository.getImage(display, REMOVE));
    messages.setLanguageText(remove, "vuzeManager.config.rules.remove");
    messages.setLanguageTooltip(remove, "vuzeManager.config.rules.remove");
    remove.setEnabled(false);
    remove.addListener(SWT.Selection, e -> handleRemoveItem());

    deleteCompleted = new Button(toolBar, SWT.PUSH);
    deleteCompleted.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, false, false).build());
    messages.setLanguageText(deleteCompleted, "vuzeManager.config.rules.checkCompleted");
    deleteCompleted.setEnabled(false);
    deleteCompleted.addListener(SWT.Selection, e -> handleDeleteCompleted());


    deleteWatched = new Button(toolBar, SWT.PUSH);
    deleteWatched.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, false, false).build());
    messages.setLanguageText(deleteWatched, "vuzeManager.config.rules.checkWatched");
    deleteWatched.setEnabled(false);
    deleteWatched.addListener(SWT.Selection, e -> handleCheckWatched());

    fakeDelete = new Button(toolBar, SWT.CHECK);
    deleteWatched.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, false, false).build());
    fakeDelete.setText(messages.getString("vuzeManager.config.rules.fakeDelete"));
    fakeDelete.setSelection(config.get(FAKE_DELETE, false));

    table = new Table(rulesSection,
        SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    table.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, true, true).build());
    table.setHeaderVisible(true);

    qualifier = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(qualifier, "vuzeManager.config.rules.column.name");
    qualifier.setWidth(200);
    setColumnWidth(qualifier, QUALIFIER_WIDTH);

    action = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(action, "vuzeManager.config.rules.column.action");
    action.setWidth(250);
    setColumnWidth(action, ACTION_WIDTH);

    arg = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(arg, "vuzeManager.config.rules.column.arg");
    arg.setWidth(300);
    setColumnWidth(arg, ARG_WIDTH);

    //listener to deselect if outside an item
    table.addListener(SWT.Selection, event -> remove.setEnabled(table.getSelection().length >= 1));
    table.addListener (SWT.MouseDoubleClick, event -> handleItemDoubleClick());

    populateTable();
    save();
  }

  private void setColumnWidth(TableColumn column, String name) {
    final int width = config.get(name, -1);
    if (width != -1) {
      column.setWidth(width);
    }
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

  private void updateDeleterButtons() {
    deleteCompleted.setEnabled(rules.getCount(Rule.Action.CATEGORY_AUTO_DELETE) > 0);
    deleteWatched.setEnabled(rules.getCount(Rule.Action.WATCHED_AUTO_DELETE) > 0);
  }

  private void updateRule(Rule oldRule, Rule newRule) {
    rules.update(oldRule, newRule);
    populateTable();
  }

  private void handleRemoveItem() {
    final TableItem[] items = table.getSelection();
    if(items.length == 1){
      final Rule rule = (Rule) items[0].getData();
      rules.remove(rule);
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

  private void handleDeleteCompleted() {
    parentSection.save();
    scheduler.schedule(() -> categoryAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS);
  }

  private void handleCheckWatched() {
    parentSection.save();
    scheduler.schedule(() -> plexAutoDeleter.autoDeleteDownloads(), 0, TimeUnit.MILLISECONDS);
  }

  private void populateTable() {
    updateDeleterButtons();
    try {
      if (table != null && !table.isDisposed()) {
        table.removeAll();
        final List<Rule> sorted = new ArrayList<>(rules);
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
    item.setText(1, messages.getString(action.getMessageKey()));
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

  @Override
  public void delete() {
    save();
    imageRepository.unLoadImages();
  }

  @Override
  public void save() {
    rules.setConfig();
    config.set(FAKE_DELETE, fakeDelete.getSelection());
    config.set(QUALIFIER_WIDTH, qualifier.getWidth());
    config.set(ACTION_WIDTH, action.getWidth());
    config.set(ARG_WIDTH, arg.getWidth());
  }
}
