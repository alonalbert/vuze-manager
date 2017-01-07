package com.alon.vuze.vuzemanager.ui;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.utils.GridDataBuilder;
import com.alon.vuze.vuzemanager.utils.UiUtils;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import javax.inject.Inject;

public class ProperSection extends Composite implements ConfigSection {
  public static final String PROPER_DIRECTORY = "properDirectory";
  public static final String PROPER_CATEGORY = "properCategory";

  @Inject
  private Config config;

  @Inject
  private Messages messages;

  private final Group group;
  private Text direcotry;
  private Text category;

  @AssistedInject
  public ProperSection(@Assisted Composite parent) {
    super(parent, SWT.NONE);

    setLayout(new GridLayout());
    setLayoutData(new GridDataBuilder(GridData.FILL, GridData.CENTER, false, false).build());
    group = new Group(getParent(), SWT.NONE);
  }

  @Override
  public void initialize(ConfigSection parent) {
    group.setText(messages.getString("vuzeManager.config.proper.title"));
    group.setLayout(new GridLayout(5, false));
    group.setLayoutData(new GridData());

    final Label dirLabel = new Label(group, SWT.NONE);
    dirLabel.setText(messages.getString("vuzeManager.config.proper.directory"));
    dirLabel.setLayoutData(new GridDataBuilder().build());
    direcotry = new Text(group, SWT.BORDER);
    direcotry.setText(config.get(PROPER_DIRECTORY, ""));
    direcotry.setLayoutData(new GridDataBuilder().setWidthHint(250).build());

    final Button browse = new Button(group, SWT.PUSH);
    browse.setText("...");
    browse.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, false, false).build());
    browse.addListener(SWT.Selection, event -> onBrowse());

    final Label categoryLabel = new Label(group, SWT.NONE);
    categoryLabel.setText(messages.getString("vuzeManager.config.proper.category"));
    categoryLabel.setLayoutData(new GridDataBuilder().build());
    category = new Text(group, SWT.BORDER);
    category.setText(config.get(PROPER_CATEGORY, ""));
    category.setLayoutData(new GridDataBuilder().setWidthHint(100).build());

  }

  private void onBrowse() {
    UiUtils.selectDirectory(
        getShell(),
        messages.getString("vuzeManager.directory.browse.title"),
        messages.getString("vuzeManager.directory.browse.message"),
        direcotry);
  }

  @Override
  public void save() {
    config.set(PROPER_DIRECTORY, direcotry.getText());
    config.set(PROPER_CATEGORY, category.getText());
  }

  @Override
  public void delete() {

  }
}
