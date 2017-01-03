package com.alon.vuze.vuzemanager.categories;

import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.ADD;

import com.alon.vuze.vuzemanager.categories.CategoryConfig.Action;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
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

@SuppressWarnings({"BindingAnnotationWithoutInject", "WeakerAccess"})
public class CategoryDialog {

  private final Display display;
  private final Shell shell;

  private final OnOkListener onOkListener;
  private final Text categoryEdit;
  private final Combo actionCombo;
  private final Spinner daysSpinner;
  private Label daysLabel;

  @AssistedInject
  CategoryDialog(
      Messages messages,
      @Assisted Display display,
      @Assisted OnOkListener onOkListener) {
    this(messages, display, onOkListener, null);
  }

  @AssistedInject
  CategoryDialog(
      Messages messages,
      @Assisted Display display,
      @Assisted OnOkListener onOkListener,
      @SuppressWarnings("SameParameterValue")
      @Assisted CategoryConfig categoryConfig) {
    this.display = display;
    this.onOkListener = onOkListener;
    shell = new Shell();
    shell.setLayout(new GridLayout());

    messages.setLanguageText(shell, "vuzeManager.categories.add.popup.title");
    shell.setImage(ImageRepository.getImage(display, ADD));

    final Composite body = new Composite(shell, SWT.BORDER);
    body.setLayout(new GridLayout(2, false));
    body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Label categoryLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(categoryLabel, "vuzeManager.categories.add.popup.category");
    final GridData labelLayout = new GridData();
    labelLayout.widthHint = 200;
    categoryLabel.setLayoutData(labelLayout);

    categoryEdit = new Text(body, SWT.SINGLE | SWT.BORDER);
    final GridData valueLayout = new GridData(GridData.FILL_HORIZONTAL);
    valueLayout.widthHint = 250;
    categoryEdit.setLayoutData(valueLayout);

    final Label actionLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(actionLabel, "vuzeManager.categories.add.popup.action");
    actionLabel.setLayoutData(labelLayout);

    actionCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
    actionCombo.setLayoutData(valueLayout);
    for (Action action : Action.values()) {
      actionCombo.add(messages.getString(action.getMessageKey()));
    }
    daysLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(daysLabel, "vuzeManager.categories.add.popup.days");
    daysLabel.setLayoutData(labelLayout);

    daysSpinner = new Spinner(body, SWT.SINGLE | SWT.BORDER);
    daysSpinner.setLayoutData(valueLayout);
    daysSpinner.setSelection(7);
    daysSpinner.setMinimum(1);

    actionCombo.addModifyListener(e -> onActionChanged());
    actionCombo.setText(actionCombo.getItem(0));

    final Composite buttons = new Composite(shell, SWT.NULL);
    buttons.setLayout(new GridLayout(2, false));
    buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Button cancel = new Button(buttons, SWT.PUSH);
    cancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
    messages.setLanguageText(cancel, "vuzeManager.categories.add.popup.cancel");
    cancel.addListener(SWT.Selection, event -> onCancel());

    final Button ok = new Button(buttons, SWT.PUSH);
    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    messages.setLanguageText(ok, "vuzeManager.categories.add.popup.ok");
    ok.addListener(SWT.Selection, event -> onOk());

    if (categoryConfig != null) {
      categoryEdit.setText(categoryConfig.getCategory());
      actionCombo.setText(actionCombo.getItem(categoryConfig.getAction().ordinal()));
      daysSpinner.setSelection(categoryConfig.getDays());
    }
  }

  private void onCancel() {
    shell.dispose();
  }

  private void onOk() {
    final String category = categoryEdit.getText();
    if (!category.isEmpty()) {
      final Action action = Action.values()[actionCombo.getSelectionIndex()];
      final CategoryConfig categoryConfig = new CategoryConfig(category, action,
          daysSpinner.getSelection());
      shell.dispose();
      if (onOkListener != null) {
        onOkListener.onOk(categoryConfig);
      }
    }
  }

  private void onActionChanged() {
    final Action action = Action.values()[actionCombo.getSelectionIndex()];
    switch (action) {
      case FORCE_SEED:
        daysSpinner.setVisible(false);
        daysLabel.setVisible(false);
        break;
      case AUTO_DELETE:
        daysSpinner.setVisible(true);
        daysLabel.setVisible(true);
        break;
    }
  }

  void open() {
    shell.pack();
    final Point pt = display.getCursorLocation();
    shell.setLocation(pt.x - 50, pt.y + 50);

    shell.open();
  }

  interface OnOkListener {

    void onOk(CategoryConfig categoryConfig);
  }
}
