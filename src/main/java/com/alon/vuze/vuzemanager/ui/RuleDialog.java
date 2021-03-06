package com.alon.vuze.vuzemanager.ui;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.alon.vuze.vuzemanager.plex.Directory;
import com.alon.vuze.vuzemanager.plex.PlexClient;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.rules.Rule;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

@SuppressWarnings({"BindingAnnotationWithoutInject", "WeakerAccess"})
public class RuleDialog {

  public static final String LAST_USED_ACTION = "ruleDialog.lastUsedAction";
  public static final String DIRECTORY_HISTORY = "ruleDialog.directoryHistory";
  public static final String RULE_DIALOG_LOCATION = "ruleDialog.location";
  private static final String RULE_DIALOG_SIZE = "ruleDialog.ruleDialogSize";

  @SuppressWarnings("unused")
  @Inject
  private Config config;

  @SuppressWarnings("unused")
  @Inject
  private Messages messages;

  @SuppressWarnings("unused")
  @Inject
  Provider<PlexClient> plexClientProvider;

  @SuppressWarnings("unused")
  @Inject
  private Logger logger;

  @Inject
  @Named(TorrentAttribute.TA_CATEGORY)
  TorrentAttribute categoryAttribute;

  private final Display display;
  private final Shell shell;

  private final OnOkListener onOkListener;

  private Combo wildcardCombo;
  private Label wildcardLabel;
  private Combo actionCombo;
  private Spinner daysSpinner;
  private Combo directoryCombo;
  private Composite days;
  private Composite directory;

  private LinkedList<String> directoryHistory = new LinkedList<>();

  @AssistedInject
  public RuleDialog(
      @Assisted Display display,
      @Assisted OnOkListener onOkListener) {
    this.display = display;
    this.onOkListener = onOkListener;
    shell = new Shell();
  }

  public void initializeAndOpen(Rule rule) {
    directoryHistory = config.get(DIRECTORY_HISTORY, new LinkedList<>());

    shell.setLayout(new GridLayout());
    messages.setLanguageText(shell, "vuzeManager.rulesDialog.title");

    final GridLayout twoColumnLayout = new GridLayout(2, false);

    final Composite body = new Composite(shell, SWT.BORDER);
    body.setLayout(twoColumnLayout);
    body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    wildcardLabel = new Label(body, SWT.NULL);
    final GridData labelLayout = new GridData();
    labelLayout.widthHint = 200;
    wildcardLabel.setLayoutData(labelLayout);

    wildcardCombo = new Combo(body, SWT.DROP_DOWN);

    final GridData valueLayout = new GridData(GridData.FILL_HORIZONTAL);
    valueLayout.widthHint = 250;
    wildcardCombo.setLayoutData(valueLayout);

    final Label actionLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(actionLabel, "vuzeManager.rulesDialog.action");
    actionLabel.setLayoutData(labelLayout);

    actionCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
    actionCombo.setLayoutData(valueLayout);
    for (Rule.Action action : Rule.Action.values()) {
      actionCombo.add(messages.getString(action.getMessageKey()));
    }

    final GridData twoSpanData = new GridData(SWT.FILL, SWT.CENTER, false, false);
    twoSpanData.horizontalSpan = 2;

    days = new Composite(body, SWT.NULL);
    days.setLayout(twoColumnLayout);
    days.setLayoutData(twoSpanData);

    final Label daysLabel = new Label(days, SWT.NULL);
    messages.setLanguageText(daysLabel, "vuzeManager.rulesDialog.days");
    daysLabel.setLayoutData(labelLayout);

    daysSpinner = new Spinner(days, SWT.SINGLE | SWT.BORDER);
    daysSpinner.setLayoutData(valueLayout);
    daysSpinner.setSelection(7);
    daysSpinner.setMinimum(1);

    directory = new Composite(body, SWT.NULL);
    directory.setLayout(new GridLayout(3, false));
    directory.setLayoutData(twoSpanData);

    final Label directoryLabel = new Label(directory, SWT.NULL);
    messages.setLanguageText(directoryLabel, "vuzeManager.rulesDialog.directory");
    directoryLabel.setLayoutData(labelLayout);

    directoryCombo = new Combo(directory, SWT.SINGLE | SWT.BORDER);
    directoryCombo.setLayoutData(valueLayout);

    final Button browse = new Button(directory, SWT.NULL);
    messages.setLanguageText(browse, "vuzeManager.rulesDialog.directory.browse");
    browse.addListener(SWT.Selection, event -> onBrowse(shell));

    actionCombo.addModifyListener(e -> onActionChanged());
    actionCombo.setText(actionCombo.getItem(config.get(LAST_USED_ACTION, Rule.Action.AUTO_DESTINATION).ordinal()));

    final Composite buttons = new Composite(shell, SWT.NULL);
    buttons.setLayout(twoColumnLayout);
    buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Button cancel = new Button(buttons, SWT.PUSH);
    cancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
    messages.setLanguageText(cancel, "vuzeManager.rulesDialog.cancel");
    cancel.addListener(SWT.Selection, event -> onCancel());

    final Button ok = new Button(buttons, SWT.PUSH);
    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    messages.setLanguageText(ok, "vuzeManager.rulesDialog.ok");
    ok.addListener(SWT.Selection, event -> onOk());

    if (rule != null) {
      wildcardCombo.setText(rule.getCategory());
      final Rule.Action action = rule.getAction();
      actionCombo.setText(actionCombo.getItem(action.ordinal()));
      if (action == Rule.Action.CATEGORY_AUTO_DELETE || action == Rule.Action.WATCHED_AUTO_DELETE) {
        daysSpinner.setSelection(rule.getArgAsInt());
      } else if (action == Rule.Action.AUTO_DESTINATION) {
        directoryCombo.setText(rule.getArg());
      }
    }
    shell.pack();
    final Point configLocation = config.get(RULE_DIALOG_LOCATION, Point.class);
    final Point location = configLocation != null ? configLocation : display.getCursorLocation();

    final Point configSize = config.get(RULE_DIALOG_SIZE, Point.class);
    if (configSize != null) {
      shell.setSize(configSize.x, configSize.y);
    }
    shell.setLocation(location.x - 50, location.y + 50);
    shell.open();
  }

  private void saveConfig() {
    config.set(RULE_DIALOG_LOCATION, shell.getLocation());
    config.set(RULE_DIALOG_SIZE, shell.getSize());
    config.save();
  }

  private void onBrowse(Shell shell) {
    final DirectoryDialog dlg = new DirectoryDialog(shell);
    final String filter;
    final String text = directoryCombo.getText();
    if (!text.isEmpty()) {
      filter = text;
    } else {
      if (!directoryHistory.isEmpty()) {
        filter = new File(directoryHistory.getFirst()).getParent();
      } else {
        filter = "";
      }
    }
    dlg.setFilterPath(filter);

    dlg.setText(messages.getString("vuzeManager.directory.browse.title"));
    dlg.setMessage(messages.getString("vuzeManager.directory.browse.message"));
    final String dir = dlg.open();
    if (dir != null) {
      directoryCombo.setText(dir);
    }
  }

  private void onOk() {
    if (validateData()) {
      final String wildcard = wildcardCombo.getText();
      final Rule.Action action = getAction();

      final Rule rule;
      switch (action) {
        case FORCE_SEED:
          rule = new Rule(wildcard, action, "");
          break;
        case CATEGORY_AUTO_DELETE:
        case WATCHED_AUTO_DELETE:
          rule = new Rule(wildcard, action,
              String.valueOf(daysSpinner.getSelection()));
          break;
        case AUTO_DESTINATION:
          final String directory = directoryCombo.getText();
          rule = new Rule(wildcard, action, directory);
          addDirectoryHistory(directory);
          break;
        default:
          throw new RuntimeException("Should never happen");
      }
      config.set(LAST_USED_ACTION, action);
      saveConfig();
      shell.dispose();
      if (onOkListener != null) {
        onOkListener.onOk(rule);
      }
    }
  }

  @SuppressWarnings("RedundantIfStatement")
  private boolean validateData() {
    if (wildcardCombo.getText().trim().isEmpty()) {
      return false;
    }
    if (actionCombo.getSelectionIndex() == Rule.Action.AUTO_DESTINATION.ordinal() && directoryCombo.getText().trim().isEmpty()) {
      return false;
    }
    return true;
  }

  private void onCancel() {
    saveConfig();
    shell.dispose();
  }

  private void addDirectoryHistory(String directory) {
    directoryHistory.removeIf(Predicate.isEqual(directory));
    directoryHistory.addFirst(directory);
    config.set(DIRECTORY_HISTORY, directoryHistory);
    config.save();
  }

  private Rule.Action getAction() {
    return Rule.Action.values()[actionCombo.getSelectionIndex()];
  }

  private void onActionChanged() {
    final String wildcardComboText = wildcardCombo.getText();
    final Rule.Action action = getAction();
    switch (action) {
      case FORCE_SEED:
        setQualifierToCategory();
        days.setVisible(false);
        directory.setVisible(false);
        break;
      case CATEGORY_AUTO_DELETE:
        setQualifierToCategory();
        days.setVisible(true);
        directory.setVisible(false);
        break;
      case WATCHED_AUTO_DELETE:
        setQualifierToSection();
        days.setVisible(true);
        directory.setVisible(false);
        break;
      case AUTO_DESTINATION:
        setQualifierToTorrentName();
        days.setVisible(false);
        directory.setVisible(true);
        break;
    }
    wildcardCombo.setText(wildcardComboText);
  }

  private void setQualifierToCategory() {
    messages.setLanguageText(wildcardLabel, "vuzeManager.rulesDialog.category");
    wildcardCombo.removeAll();
    for (String category : categoryAttribute.getDefinedValues()) {
      wildcardCombo.add(category);
    }
  }

  private void setQualifierToSection() {
    messages.setLanguageText(wildcardLabel, "vuzeManager.rulesDialog.section");
    wildcardCombo.removeAll();
    try {
      final Collection<Directory> sections = plexClientProvider.get().getShowSections();
      sections.forEach(section -> wildcardCombo.add(section.getTitle()));
    } catch (IOException | SAXException | XPathExpressionException e) {
      logger.log(e, "Failed to load sections");
    }
  }

  private void setQualifierToTorrentName() {
    messages.setLanguageText(wildcardLabel, "vuzeManager.rulesDialog.torrent");
    wildcardCombo.removeAll();
    directoryHistory.forEach(directory -> directoryCombo.add(directory));
  }

  @SuppressWarnings("unused")
  public boolean isDisposed() {
    return shell.isDisposed();
  }

  public interface OnOkListener {
    void onOk(Rule rule);
  }
}
