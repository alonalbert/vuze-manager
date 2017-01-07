package com.alon.vuze.vuzemanager.ui;

interface ConfigSection {
  void initialize(ConfigSection parent);
  void save();
  void delete();
}
