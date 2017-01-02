package com.alon.vuze.vuzemanager.testing;

import static com.alon.vuze.vuzemanager.resources.ImageRepository.ImageResource.ADD;

import com.alon.vuze.vuzemanager.Config;
import com.alon.vuze.vuzemanager.categories.CategoryConfig;
import com.alon.vuze.vuzemanager.logger.DebugLogger;
import com.alon.vuze.vuzemanager.resources.DebugMessages;
import com.alon.vuze.vuzemanager.resources.ImageRepository;
import java.io.IOException;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

class SwtMain {

  public static void main(String[] args) throws IOException {
    final Config config = new Config("out", new DebugLogger());
    final DebugMessages messages = new DebugMessages();
    final Display display = new Display();
    final Shell shell = new Shell();
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

    final Text categoryEdit = new Text(body, SWT.SINGLE | SWT.BORDER);
    final GridData valueLayout = new GridData(GridData.FILL_HORIZONTAL);
    valueLayout.widthHint = 250;
    categoryEdit.setLayoutData(valueLayout);

    final Label actionLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(actionLabel, "vuzeManager.categories.add.popup.action");
    actionLabel.setLayoutData(labelLayout);

    final Combo actionCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
    actionCombo.setLayoutData(valueLayout);
    for (CategoryConfig.Action action : CategoryConfig.Action.values()) {
      actionCombo.add(messages.getString(action.getMessageKey()));
    }
    actionCombo.setText(actionCombo.getItem(0));

    final Label daysLabel = new Label(body, SWT.NULL);
    messages.setLanguageText(daysLabel, "vuzeManager.categories.add.popup.days");
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
    messages.setLanguageText(cancel, "vuzeManager.categories.add.popup.cancel");
    cancel.addListener(SWT.Selection, event -> shell.dispose());

    final Button ok = new Button(buttons, SWT.PUSH);
    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    messages.setLanguageText(ok, "vuzeManager.categories.add.popup.ok");
    ok.addListener(SWT.Selection, event -> {
      final String category = categoryEdit.getText();
      if (!category.isEmpty()) {
        final Set<CategoryConfig> categories = config.getCategories();
        final CategoryConfig.Action action = CategoryConfig.Action.values()[actionCombo
            .getSelectionIndex()];
        final CategoryConfig categoryConfig = new CategoryConfig(category, action,
            daysSpinner.getSelection());
        if (categories.contains(categoryConfig)) {
          categories.remove(categoryConfig);
        }
        categories.add(categoryConfig);
        shell.dispose();
        populateTable(categories);
        config.save();
      }
    });

    //open shell
    shell.pack();
    final Monitor primary = display.getPrimaryMonitor ();
    final Rectangle bounds = primary.getBounds ();
    final Rectangle rect = shell.getBounds ();
    shell.setLocation (
        bounds.x + (bounds.width - rect.width) / 2,
        bounds.y +(bounds.height - rect.height) / 2);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();

  }

  private static void populateTable(Set<CategoryConfig> categories) {
    categories.forEach(System.out::println);
  }
}
