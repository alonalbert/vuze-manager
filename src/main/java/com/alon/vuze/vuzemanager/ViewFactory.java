package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.ui.ConfigView;
import com.alon.vuze.vuzemanager.ui.PlexSection;
import com.alon.vuze.vuzemanager.ui.ProperSection;
import com.alon.vuze.vuzemanager.ui.RuleDialog;
import com.alon.vuze.vuzemanager.ui.RulesSection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public interface ViewFactory {
  ConfigView createConfigView(Composite parent);

  RulesSection createSectionView(Composite parent);

  ProperSection createProperSection(Composite parent);

  PlexSection createPlexSection(Composite parent);

  RuleDialog createRunDialog(Display display, RuleDialog.OnOkListener onOkListener);
}
