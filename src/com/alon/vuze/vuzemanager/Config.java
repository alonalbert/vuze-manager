package com.alon.vuze.vuzemanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

// TODO: 12/31/16 Use JSON
public class Config {
  private final String configFile;
  private final DebugLogger logger;

  private Set<CategoryConfig> categories = new HashSet<>();

  public Config(String path, DebugLogger logger) {
    this.configFile = path + "vuze-manager.options";
    this.logger = logger;
    load();
  }

  Set<CategoryConfig> getCategories() {
    return categories;
  }

  public synchronized void save() {
    final File optionsFile = new File(configFile);
    logger.log("storing options to file: %s", optionsFile.getPath());
    try {
      if (!optionsFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        optionsFile.createNewFile();
      }
      saveFile(optionsFile);
    } catch(Exception e) {
      logger.log(e, "Failed to store options");
    }
  }

  private synchronized void load() {
    final File optionsFile = new File(configFile);
    logger.log("loading options from file: %s", optionsFile.getPath());
    try {
      if(optionsFile.exists()) {
        loadFile(optionsFile);
      } else {
        //noinspection ResultOfMethodCallIgnored
        optionsFile.createNewFile();
        saveFile(optionsFile);
      }
    } catch(Exception e) {
      logger.log(e, "Failed to load options");
    }
  }

  private void saveFile(File file) throws IOException {
    try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
      out.writeObject(categories);
    }
  }

  private void loadFile(File optionsFile) throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(optionsFile)))) {
      //noinspection unchecked
      categories = (Set<CategoryConfig>) in.readObject();
    }
  }
}
