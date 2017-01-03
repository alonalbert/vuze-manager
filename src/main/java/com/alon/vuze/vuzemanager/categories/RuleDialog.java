package com.alon.vuze.vuzemanager.categories;

import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.ADD;

import com.alon.vuze.vuzemanager.categories.Rule.Action;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

@SuppressWarnings({"BindingAnnotationWithoutInject", "WeakerAccess"})
public class RuleDialog {

  private final Messages messages;
  private final TorrentAttribute categoryAttribute;
  private final Display display;
  private final Shell shell;

  private final OnOkListener onOkListener;

  private final Combo wildcardCombo;
  private Label wildcardLabel;

  private final Combo actionCombo;

  private final Spinner daysSpinner;

  private Label directoryLabel;
  private Text directoryText;
  private Composite days;
  private final Composite directory;

  @AssistedInject
  RuleDialog(
      Messages messages,
      @Named(TorrentAttribute.TA_CATEGORY) TorrentAttribute categoryAttribute,
      @Assisted Display display,
      @Assisted OnOkListener onOkListener) {
    this(messages, categoryAttribute, display, onOkListener, null);
  }

  @AssistedInject
  RuleDialog(
      Messages messages,
      @Named(TorrentAttribute.TA_CATEGORY) TorrentAttribute categoryAttribute,
      @Assisted Display display,
      @Assisted OnOkListener onOkListener,
      @Assisted Rule rule) {
    this.messages = messages;
    this.categoryAttribute = categoryAttribute;
    this.display = display;
    this.onOkListener = onOkListener;
    shell = new Shell();
    shell.setLayout(new GridLayout());

    messages.setLanguageText(shell, "vuzeManager.categories.add.popup.title");
    shell.setImage(ImageRepository.getImage(display, ADD));

    final GridLayout twoColumnLayout = new GridLayout(2, false);

    final Composite body = new Composite(shell, SWT.BORDER);
    body.setLayout(twoColumnLayout);
    body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    wildcardLabel = new Label(body, SWT.NULL);
    final GridData labelLayout = new GridData();
    labelLayout.widthHint = 200;
    wildcardLabel.setLayoutData(labelLayout);

    wildcardCombo = new Combo(body, SWT.DROP_DOWN);

    final GridData valueLayout = new GridData(GridData.FILL_HORIZONTAL);
    valueLayout.widthHint = 250;
    wildcardCombo.setLayoutData(valueLayout);

    final Label actionLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(actionLabel, "vuzeManager.categories.add.popup.action");
    actionLabel.setLayoutData(labelLayout);

    actionCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
    actionCombo.setLayoutData(valueLayout);
    for (Action action : Action.values()) {
      actionCombo.add(messages.getString(action.getMessageKey()));
    }

    final GridData twoSpanData = new GridData();
    twoSpanData.horizontalSpan = 2;

    days = new Composite(body, SWT.NULL);
    days.setLayout(twoColumnLayout);
    days.setLayoutData(twoSpanData);

    final Label daysLabel = new Label(days, SWT.NULL);
    messages.setLanguageText(daysLabel, "vuzeManager.categories.add.popup.days");
    daysLabel.setLayoutData(labelLayout);

    daysSpinner = new Spinner(days, SWT.SINGLE | SWT.BORDER);
    daysSpinner.setLayoutData(valueLayout);
    daysSpinner.setSelection(7);
    daysSpinner.setMinimum(1);

    directory = new Composite(body, SWT.NULL);
    directory.setLayout(twoColumnLayout);
    directory.setLayoutData(twoSpanData);

    directoryLabel = new Label(directory, SWT.NULL);
    messages.setLanguageText(directoryLabel, "vuzeManager.categories.add.popup.directory");
    directoryLabel.setLayoutData(labelLayout);

    directoryText = new Text(directory, SWT.SINGLE | SWT.BORDER);
    directoryText.setLayoutData(valueLayout);

    actionCombo.addModifyListener(e -> onActionChanged());
    actionCombo.setText(actionCombo.getItem(0));

    final Composite buttons = new Composite(shell, SWT.NULL);
    buttons.setLayout(twoColumnLayout);
    buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Button cancel = new Button(buttons, SWT.PUSH);
    cancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
    messages.setLanguageText(cancel, "vuzeManager.categories.add.popup.cancel");
    cancel.addListener(SWT.Selection, event -> onCancel());

    final Button ok = new Button(buttons, SWT.PUSH);
    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    messages.setLanguageText(ok, "vuzeManager.categories.add.popup.ok");
    ok.addListener(SWT.Selection, event -> onOk());

    if (rule != null) {
      wildcardCombo.setText(rule.getCategory());
      final Action action = rule.getAction();
      actionCombo.setText(actionCombo.getItem(action.ordinal()));
      if (action == Action.CATEGORY_AUTO_DELETE || action == Action.WATCHED_AUTO_DELETE) {
        daysSpinner.setSelection(rule.getArgAsInt());
      } else if (action == Action.AUTO_DESTINATION) {
        directoryText.setText(rule.getArg());
      }
    }
  }

  private void onCancel() {
    shell.dispose();
  }

  private void onOk() {
    final String wildcard = wildcardCombo.getText();
    if (!wildcard.isEmpty()) {
      final Action action = getAction();

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
          rule = new Rule(wildcard, action, directoryText.getText());
          break;
        default:
          throw new RuntimeException("Should never happen");
      }

      shell.dispose();
      if (onOkListener != null) {
        onOkListener.onOk(rule);
      }
    }
  }

  private Action getAction() {
    return Action.values()[actionCombo.getSelectionIndex()];
  }

  private void onActionChanged() {
    final Action action = getAction();
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
  }

  private void setQualifierToCategory() {
    messages.setLanguageText(wildcardLabel, "vuzeManager.categories.add.popup.category");
    wildcardCombo.removeAll();
    for (String category : categoryAttribute.getDefinedValues()) {
      wildcardCombo.add(category);
    }
  }

  private void setQualifierToSection() {
    messages.setLanguageText(wildcardLabel, "vuzeManager.categories.add.popup.section");
    wildcardCombo.removeAll();
  }

  private void setQualifierToTorrentName() {
    messages.setLanguageText(wildcardLabel, "vuzeManager.categories.add.popup.torrent");
    wildcardCombo.removeAll();
  }

  void open() {
    shell.pack();
    final Point pt = display.getCursorLocation();
    shell.setLocation(pt.x - 50, pt.y + 50);
    shell.open();
  }

  interface OnOkListener {

    void onOk(Rule rule);
  }
}
