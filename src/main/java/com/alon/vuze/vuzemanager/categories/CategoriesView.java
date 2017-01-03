package com.alon.vuze.vuzemanager.categories;

import static com.alon.vuze.vuzemanager.categories.Rule.Action.FORCE_SEED;
import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.ADD;
import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.REMOVE;
import static org.gudy.azureus2.ui.swt.Utils.getDisplay;

import com.alon.vuze.vuzemanager.Config;
import com.alon.vuze.vuzemanager.categories.Rule.Action;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
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
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

@SuppressWarnings("WeakerAccess")
public class CategoriesView implements UISWTViewEventListener, DownloadCompletionListener {

  @Inject
  private Config config;

  @Inject
  private Logger logger;

  @Inject
  private Messages messages;

  @Inject
  DownloadManager downloadManager;

  @SuppressWarnings("WeakerAccess")
  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  TorrentAttribute categoryAttribute;

  @SuppressWarnings("WeakerAccess")
  @Inject
  @Named(CategoriesModule.TA_COMPLETED_TIME)
  TorrentAttribute completedTimeAttribute;

  @Inject
  CategoriesModule.Factory factory;

  private Table table;
  private ToolItem remove;

  @Inject
  public CategoriesView() {
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
    messages.setLanguageText(label, "vuzeManager.categories.label");

    final ToolBar toolBar = new ToolBar(root, SWT.BORDER | SWT.FLAT);

    final ToolItem add = new ToolItem(toolBar, SWT.PUSH);
    add.setImage(ImageRepository.getImage(display, ADD));
    messages.setLanguageTooltip(add, "vuzeManager.categories.add");
    add.addListener(SWT.Selection, e -> handleAddItem());

    table = new Table(root,
        SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    remove = new ToolItem(toolBar, SWT.PUSH);
    remove.setImage(ImageRepository.getImage(display, REMOVE));
    messages.setLanguageTooltip(remove, "vuzeManager.categories.remove");
    remove.setEnabled(false);
    remove.addListener(SWT.Selection, e -> handleRemoveItem());

    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);

    // TODO: 12/31/16 Persist column widths in config
    final TableColumn name = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(name, "vuzeManager.categories.column.name");
    name.setWidth(200);

    final TableColumn action = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(action, "vuzeManager.categories.column.action");
    action.setWidth(250);

    final TableColumn days = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(days, "vuzeManager.categories.column.arg");
    days.setWidth(600);

    //listener to deselect if outside an item
    table.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent event) {
        if (event.button == 1) {
          if (table.getItem(new Point(event.x, event.y)) == null) {
            table.deselectAll();
            remove.setEnabled(false);
          }
        }
      }
    });
    table.addListener(SWT.Selection, event -> remove.setEnabled(table.getSelection().length >= 1));
    table.addListener (SWT.MouseDoubleClick, event -> handleItemDoubleClick());

    populateTable();

    final DownloadEventNotifier eventNotifier = downloadManager.getGlobalDownloadEventNotifier();
    eventNotifier.addCompletionListener(this);
  }

  private void delete() {
    ImageRepository.unLoadImages();
  }

  @Override
  public void onCompletion(Download download) {
    download.setLongAttribute(completedTimeAttribute, System.currentTimeMillis());

    final String category = download.getAttribute(categoryAttribute);
    if (category == null) {
      return;
    }
    final Set<Rule> rules = config.getRules();
    rules.stream()
        .filter(rule -> rule.getAction() == FORCE_SEED && rule.getWildcard().matches(category))
        .forEach(rule -> forceStart(download));
  }

  private void forceStart(Download download) {
    logger.log("Download %s force started.", download);
    download.setForceStart(true);
  }

  private void handleItemDoubleClick() {
    final TableItem[] items = table.getSelection();
    if (items.length == 1) {
      final Rule rule = (Rule) items[0].getData();
      final RuleDialog ruleDialog = factory.create(
          getDisplay(),
          newRule -> handleAddedOrEdited(rule, newRule));
      ruleDialog.initializeAndOpen(rule);
    }
  }

  private void handleAddItem() {
    final RuleDialog ruleDialog = factory.create(
        getDisplay(),
        rule -> handleAddedOrEdited(null, rule));
    ruleDialog.initializeAndOpen(null);
  }

  private void handleAddedOrEdited(Rule oldConfig, Rule newConfig) {
    final Set<Rule> categories = config.getRules();
    if (oldConfig != null) {
      categories.remove(oldConfig);
    }
    categories.add(newConfig);
    config.save();
    populateTable();
  }

  private void handleRemoveItem() {
    final TableItem[] items = table.getSelection();
    if(items.length == 1){
      final Rule rule = (Rule) items[0].getData();
      config.getRules().remove(rule);
      config.save();
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
        final List<Rule> sorted = new ArrayList<>(config.getRules());
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
    final Action action = rule.getAction();
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
