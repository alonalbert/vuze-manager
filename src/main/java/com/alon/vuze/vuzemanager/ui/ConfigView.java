package com.alon.vuze.vuzemanager.ui;

import com.alon.vuze.vuzemanager.ViewFactory;
import com.alon.vuze.vuzemanager.config.Config;
import com.alon.vuze.vuzemanager.utils.GridDataBuilder;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;

public class ConfigView extends Composite implements ConfigSection {

  private final Collection<ConfigSection> sections = new LinkedList<>();

  @Inject
  private Config config;

  @AssistedInject
  public ConfigView(@Assisted Composite parent, ViewFactory factory) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    setLayoutData(new GridDataBuilder(GridData.FILL, GridData.FILL, true, true).build());
    sections.add(factory.createPlexSection(this));
    sections.add(factory.createProperSection(this));
    sections.add(factory.createSectionView(this));

    for (ConfigSection section : sections) {
      section.initialize(this);
    }
  }

  public void save() {
    if (!isDisposed()) {
      for (ConfigSection section : sections) {
        section.save();
      }
    }
    config.save();
  }

  public void delete() {
    for (ConfigSection section : sections) {
      section.delete();
    }
  }

  @Override
  public void initialize(ConfigSection parent) {

  }
}
