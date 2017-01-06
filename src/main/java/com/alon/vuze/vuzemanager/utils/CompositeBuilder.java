package com.alon.vuze.vuzemanager.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("SameParameterValue")
public class CompositeBuilder {

  private final Composite composite;
  private final GridLayout layout;

  public CompositeBuilder(Composite parent) {
    this(parent, SWT.NONE, 1);
  }

  public CompositeBuilder(Composite parent, int style, int columns) {
    composite = new Composite(parent, style);
    layout = new GridLayout(columns, false);
  }


  public CompositeBuilder setGridData(GridDataBuilder builder) {
    composite.setLayoutData(builder.build());
    return this;
  }

  public CompositeBuilder setLayoutMarginWidth(int margin) {
    layout.marginWidth = margin;
    return this;
  }


  public Composite build() {
    composite.setLayout(layout);
    return composite;
  }
}
