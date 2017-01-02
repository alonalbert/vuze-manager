package com.alon.vuze.vuzemanager.categories;

import com.alon.vuze.vuzemanager.categories.CategoryDialog.OnOkListener;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class CategoriesModule extends AbstractModule {
public interface Factory {
    CategoriesView create(Composite parent);
    CategoryDialog create(Display display, OnOkListener onOkListener);
    CategoryDialog create(Display display, OnOkListener onOkListener, CategoryConfig categoryConfig);
 }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
     .build(Factory.class));
    bind(DownloadAutoDeleter.class).asEagerSingleton();
  }
}
