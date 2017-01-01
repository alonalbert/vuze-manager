package com.alon.vuze.vuzemanager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Config {

  private static final String CATEGORIES = "categories";
  private final String configFile;
  private final DebugLogger logger;

  private final Set<CategoryConfig> categories = new HashSet<>();

  Config(String path, DebugLogger logger) {
    this.configFile = path + "/vuze-manager-options.json";
    this.logger = logger;
    load();
  }

  Set<CategoryConfig> getCategories() {
    return categories;
  }

  synchronized void save() {
    final File optionsFile = new File(configFile);
    logger.log("storing options to file: %s", optionsFile.getPath());
    try {
      if (!optionsFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        optionsFile.createNewFile();
      }
      saveFile(optionsFile);
    } catch(IOException e) {
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
    } catch(Throwable e) {
      logger.log(e, "Failed to load options");
      try {
        saveFile(optionsFile);
      } catch (IOException e1) {
        logger.log(e, "Failed to store options");
      }
    }
  }

  private void saveFile(File file) throws IOException {
    final JSONObject json = new JSONObject();
    final JSONArray jsonCategories = new JSONArray();
    for (CategoryConfig category : categories) {
      jsonCategories.add(category.toJson());
    }
    json.put(CATEGORIES, jsonCategories);

    try (FileWriter out = new FileWriter(file)) {
      out.write(json.toString());
    }
  }

  private void loadFile(File file) throws Exception {
    final JSONParser parser = new JSONParser();

    try (FileReader in = new FileReader(file)) {
      JSONObject json = (JSONObject) parser.parse(in);

      final JSONArray jsonCategories = (JSONArray) json.get(CATEGORIES);
      for (Object obj : jsonCategories) {
        categories.add(CategoryConfig.fromJson((JSONObject) obj));
      }
    }
  }
}
