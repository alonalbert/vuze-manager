package com.alon.vuze.vuzemanager.utils;

import org.eclipse.swt.layout.GridData;

public class GridDataBuilder {

  private final GridData gridData;

  public GridDataBuilder() {
    gridData = new GridData();
  }

  public GridDataBuilder(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace) {
    gridData = new GridData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace);
  }

  public GridDataBuilder setWidthHint(int widthHint) {
    gridData.widthHint = widthHint;
    return this;
  }

  public GridData build() {
    return gridData;
  }

  public GridDataBuilder setHorizontalSpan(int span) {
    gridData.horizontalSpan = span;
    return this;
  }
}
