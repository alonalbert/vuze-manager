package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.ui.ConfigView;
import com.google.inject.Inject;
import org.eclipse.swt.widgets.Composite;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;

import javax.inject.Singleton;

@Singleton
class VuzeManagerConfigSection implements UISWTConfigSection {
  @javax.inject.Inject
  private VuzeManagerPlugin.Factory factory;
  private ConfigView configView;

  @Inject
  public VuzeManagerConfigSection() {
  }

  @Override
  public Composite configSectionCreate(Composite parent) {
    configView = factory.createConfigView(parent);
    return configView;
  }

  @Override
  public int maxUserMode() {
    return 0;
  }

  @Override
  public String configSectionGetParentSection() {
    return ConfigSection.SECTION_PLUGINS;
  }

  @Override
  public String configSectionGetName() {
    return "vuzeManager";
  }

  @Override
  public void configSectionSave() {
    configView.save();
  }

  @Override
  public void configSectionDelete() {
    configView.delete();
  }
}
