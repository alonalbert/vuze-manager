package com.alon.vuze.vuzemanager;

import com.alon.vuze.vuzemanager.Annotations.PluginDirectory;
import com.alon.vuze.vuzemanager.categories.Rule;
import com.alon.vuze.vuzemanager.categories.Rule.Action;
import com.alon.vuze.vuzemanager.logger.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Singleton
public class Config {

  private static final String RULES = "rules";
  private static final String DIRECTORIES = "directories";
  private static final String LAST_USED_ACTION = "lastUsedAction";
  public static final String COLUMN_NAME = "columnName";
  public static final String COLUMN_ACTION = "columnAction";
  public static final String COLUMN_ARG = "columnArg";
  private final String configFile;
  private final Logger logger;

  private final Set<Rule> rules = new HashSet<>();
  private final ArrayList<String> directories = new ArrayList<>();
  private Rule.Action lastUsedAction = Action.AUTO_DESTINATION;

  @Inject
  public Config(@PluginDirectory String path, Logger logger) {
    this.configFile = path + "/vuze-manager-options.json";
    this.logger = logger;
    load();
  }

  public Set<Rule> getRules() {
    return rules;
  }


  public Action getLastUsedAction() {
    return lastUsedAction;
  }

  public void setLastUsedAction(Action lastUsedAction) {
    this.lastUsedAction = lastUsedAction;
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
    } catch(IOException e) {
      logger.log(e, "Failed to store options");
    }
  }

  public ArrayList<String> getDirectories() {
    return directories;
  }

  public void addDirectory(String directory) {
    directories.removeIf(Predicate.isEqual(directory));
    directories.add(0, directory);
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

    json.put(RULES, rules.stream()
        .map(Rule::toJson)
        .collect(Collectors.toCollection(JSONArray::new)));

    final JSONArray jsonArray = new JSONArray();
    jsonArray.addAll(directories);
    json.put(DIRECTORIES, jsonArray);

    json.put(LAST_USED_ACTION, lastUsedAction.toString());

    try (FileWriter out = new FileWriter(file)) {
      out.write(json.toString());
    }
  }

  private void loadFile(File file) throws Exception {
    final JSONParser parser = new JSONParser();

    try (FileReader in = new FileReader(file)) {
      final JSONObject json = (JSONObject) parser.parse(in);

      final JSONArray jsonCategories = (JSONArray) json.get(RULES);
      rules.addAll(jsonCategories.stream()
          .map(obj -> Rule.fromJson((JSONObject) obj))
          .collect(Collectors.toList()));

      final JSONArray jsonDirectories = (JSONArray) json.get(DIRECTORIES);
      directories.addAll(jsonDirectories.stream()
          .map(Object::toString)
          .collect(Collectors.toList()));

      lastUsedAction = Action.valueOf(
          (String) json.getOrDefault(LAST_USED_ACTION, Action.AUTO_DESTINATION.name()));
    }
  }
}
