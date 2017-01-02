package com.alon.vuze.vuzemanager;

import static com.alon.vuze.vuzemanager.ImageRepository.ImageResource.ADD;
import static com.alon.vuze.vuzemanager.ImageRepository.ImageResource.REMOVE;

import java.util.ArrayList;
import java.util.Comparator;
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
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;

class CatagoriesView extends Composite implements DownloadCompletionListener {

  private final PluginInterface pluginInterface;
  private final Config config;
  private final Logger logger;
  private final Messages messages;

  private final Table table;
  private final ToolItem remove;
  private final TorrentAttribute categoryAttribute;

  CatagoriesView(Composite parent, PluginInterface pluginInterface, Config config, Logger logger, Messages messages) {
    super(parent, SWT.BORDER);
    this.pluginInterface = pluginInterface;
    this.config = config;
    this.logger = logger;
    this.messages = messages;

    final Display display = getDisplay();

    setLayout(new GridLayout());
    setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label label = new Label(this, SWT.NULL);
    messages.setLanguageText(label, "vuzeManager.categories.label");

    final ToolBar toolBar = new ToolBar(this, SWT.BORDER | SWT.FLAT);

    final ToolItem add = new ToolItem(toolBar, SWT.PUSH);
    add.setImage(ImageRepository.getImage(display, ADD));
    messages.setLanguageTooltip(add, "vuzeManager.categories.add");
    add.addListener(SWT.Selection, e -> handleAddItem());

    table = new Table(this,
        SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    remove = new ToolItem(toolBar, SWT.PUSH);
    remove.setImage(ImageRepository.getImage(display, REMOVE));
    messages.setLanguageTooltip(remove, "vuzeManager.categories.remove");
    remove.setEnabled(false);
    remove.addListener(SWT.Selection, e -> handleRemoveItem());

    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);

    // TODO: 12/31/16 Persist comulmn widths in config
    final TableColumn name = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(name, "vuzeManager.categories.column.name");
    name.setWidth(200);

    final TableColumn action = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(action, "vuzeManager.categories.column.action");
    action.setWidth(250);

    final TableColumn days = new TableColumn(table, SWT.NULL);
    messages.setLanguageText(days, "vuzeManager.categories.column.days");
    days.setWidth(200);

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

    final TorrentManager torrentManager = pluginInterface.getTorrentManager();
    categoryAttribute = torrentManager.getAttribute(TorrentAttribute.TA_CATEGORY);

    final DownloadManager downloadManager = pluginInterface.getDownloadManager();
    final DownloadEventNotifier eventNotifier = downloadManager.getGlobalDownloadEventNotifier();
    eventNotifier.addCompletionListener(this);
  }

  @Override
  public void onCompletion(Download download) {
    final String category = download.getAttribute(categoryAttribute);
    if (category == null) {
      return;
    }
    final Set<CategoryConfig> categoryConfigs = config.getCategories();
    for (CategoryConfig categoryConfig : categoryConfigs) {
      if (categoryConfig.getAction() == CategoryConfig.Action.FORCE_SEED) {
        final String wildcard = categoryConfig.getCategory();
        if (new Wildcard(wildcard).matches(category)) {
          logger.log("Download %s category %s matched wildcard '%s'. Force started.",
              download, category, wildcard);
          download.setForceStart(true);
        }
      }
    }
  }

  private void handleItemDoubleClick() {
    final TableItem[] items = table.getSelection();
    if (items.length == 1) {
      final CategoryConfig categoryConfig = (CategoryConfig) items[0].getData();
      final CategoryDialog categoryDialog = new CategoryDialog(
          getDisplay(), config, messages, categoryConfig);
      categoryDialog.setOnOkListener(newCategoryConfig -> handleAddedOrEdited(categoryConfig, newCategoryConfig));
      categoryDialog.open();
    }
  }

  private void handleAddItem() {
    final CategoryDialog categoryDialog = new CategoryDialog(getDisplay(), config, messages);
    categoryDialog.setOnOkListener(categoryConfig -> handleAddedOrEdited(null, categoryConfig));
    categoryDialog.open();
  }

  private void handleAddedOrEdited(CategoryConfig oldConfig, CategoryConfig newConfig) {
    final Set<CategoryConfig> categories = config.getCategories();
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
      final CategoryConfig categoryConfig = (CategoryConfig) items[0].getData();
      config.getCategories().remove(categoryConfig);
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
        final ArrayList<CategoryConfig> sorted = new ArrayList<>(config.getCategories());
        sorted.sort(Comparator.comparing(CategoryConfig::getCategory));
        for (CategoryConfig category : sorted) {
          addCategoryToTable(category);
        }
      }
    } catch (Exception e) {
      // TODO: 12/31/16 handle
    }
  }

  private void addCategoryToTable(CategoryConfig categoryConfig) {
    final TableItem item = new TableItem(table, SWT.NULL);
    item.setData(categoryConfig);
    item.setText(0, categoryConfig.getCategory());
    item.setText(1, MessageText.getString(categoryConfig.getAction().getMessageKey()));
    item.setText(2, String.valueOf(categoryConfig.getDays()));
  }
}
