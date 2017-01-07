package com.alon.vuze.vuzemanager.ui;

import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.resources.Messages;
import com.alon.vuze.vuzemanager.utils.CompositeBuilder;
import com.alon.vuze.vuzemanager.utils.GridDataBuilder;
import com.alon.vuze.vuzemanager.utils.NetworkUtils;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import javax.inject.Inject;

public class PlexSection extends Composite implements ConfigSection {

  public static final String PLEX_HOST = "plexHost";
  public static final String PLEX_PORT = "plexPort";
  public static final String PLEX_ROOT = "plexRoot";
  public static final String VUZE_ROOT = "vuzeRoot";

  @Inject
  private Config config;

  @Inject
  private Messages messages;

  private final Group group;

  private Text host;
  private Spinner port;
  private Text plexRoot;
  private Text vuzeRoot;

  @AssistedInject
  public PlexSection(@Assisted Composite parent) {
    super(parent, SWT.NONE);

    setLayout(new GridLayout());
    setLayoutData(new GridDataBuilder(GridData.FILL, GridData.CENTER, false, false).build());
    group = new Group(getParent(), SWT.NONE);
  }

  @Override
  public void initialize(ConfigSection parent) {
    group.setText(messages.getString("vuzeManager.config.plex.title"));
    group.setLayout(new GridLayout(4, false));
    group.setLayoutData(new GridData());

    final Label hostLabel = new Label(group, SWT.NONE);
    hostLabel.setText(messages.getString("vuzeManager.config.plex.host"));
    hostLabel.setLayoutData(new GridDataBuilder().build());
    host = new Text(group, SWT.BORDER);
    host.setText(config.get(PLEX_HOST, NetworkUtils.getLocalhostAddress()));
    host.setLayoutData(new GridDataBuilder().setWidthHint(200).build());

    final Label portLabel = new Label(group, SWT.NONE);
    portLabel.setText(messages.getString("vuzeManager.config.plex.port"));
    portLabel.setLayoutData(new GridDataBuilder().build());
    port = new Spinner(group, SWT.BORDER);
    port.setMinimum(1000);
    port.setMaximum(999999);
    port.setSelection(config.get(PLEX_PORT, 32400));
    port.setLayoutData(new GridDataBuilder().setWidthHint(150).build());

    final Label plexRootLabel = new Label(group, SWT.NONE);
    plexRootLabel.setText(messages.getString("vuzeManager.config.plex.plexRoot"));
    plexRootLabel.setLayoutData(new GridDataBuilder(GridData.CENTER, GridData.CENTER, false, false)
        .setWidthHint(100)
        .build());

    final Composite plexRootWrapper = new CompositeBuilder(group, SWT.NONE, 3)
        .setLayoutMarginWidth(0)
        .setGridData(new GridDataBuilder(GridData.FILL, GridData.CENTER, true, false)
            .setHorizontalSpan(3))
        .build();

    plexRoot = new Text(plexRootWrapper, SWT.BORDER);
    plexRoot.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, true, false)
        .setHorizontalSpan(2)
        .build());
    plexRoot.setText(config.get(PLEX_ROOT, ""));
    final Button plexRootBrowse = new Button(plexRootWrapper, SWT.PUSH);
    plexRootBrowse.setText("...");
    plexRootBrowse.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, false, false).build());
    plexRootBrowse.addListener(SWT.Selection, event -> onBrowse(plexRoot));

    final Label vuzeRootLabel = new Label(group , SWT.NONE);

    final Composite vuzeRootWrapper = new CompositeBuilder(group, SWT.NONE, 3)
        .setLayoutMarginWidth(0)
        .setGridData(new GridDataBuilder(GridData.FILL, GridData.CENTER, true, false)
            .setHorizontalSpan(3))
        .build();

    vuzeRootLabel.setText(messages.getString("vuzeManager.config.plex.vuzeRoot"));
    vuzeRootLabel.setLayoutData(new GridDataBuilder().setWidthHint(100).build());
    vuzeRoot = new Text(vuzeRootWrapper , SWT.BORDER);
    vuzeRoot.setText(config.get(VUZE_ROOT, ""));
    vuzeRoot.setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, true, false)
        .setHorizontalSpan(2)
        .build());
    final Button vuzeRootBrowse = new Button(vuzeRootWrapper , SWT.PUSH);
    vuzeRootBrowse.setText("...");
    vuzeRootBrowse.setLayoutData(new GridDataBuilder(GridData.END, GridData.CENTER, false, false).build());
    vuzeRootBrowse.addListener(SWT.Selection, event -> onBrowse(vuzeRoot));

    save();
  }

  private void onBrowse(Text target) {
    UiUtils.selectDirectory(getShell(), "Select directory", "Select directory", target);
  }

  @Override
  public void save() {
    config.set(PLEX_HOST, host.getText());
    config.set(PLEX_PORT, port.getSelection());
    config.set(PLEX_ROOT, plexRoot.getText());
    config.set(VUZE_ROOT, vuzeRoot.getText());
  }

  @Override
  public void delete() {
  }
}
