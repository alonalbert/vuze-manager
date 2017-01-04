package com.alon.vuze.vuzemanager.config;

import com.alon.vuze.vuzemanager.Annotations.PluginDirectory;
import com.alon.vuze.vuzemanager.logger.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;

public class Config {
  private final String configFile;
  private final Logger logger;

  private final GsonBuilder gsonBuilder = new GsonBuilder()
      .setPrettyPrinting()
      .excludeFieldsWithoutExposeAnnotation();

  private final Gson gson =gsonBuilder.create();

  private final Map<String, JsonElement> map;

  @Inject
  public Config(@PluginDirectory String path, Logger logger) {
    this.configFile = path + "/vuze-manager-options.json";
    this.logger = logger;
    map = load();
  }

  public void set(String key, Object value) {
    map.put(key, gson.toJsonTree(value));
  }

  public <T> T get(String key, T defaultValue) {
    final JsonElement element = map.get(key);
    if (element == null) {
      return defaultValue;
    }
    final Class<?> cls = defaultValue.getClass();
    //noinspection unchecked
    return gson.fromJson(element, (Class<T>) cls);
  }

  public <T> T get(String key, Class<T> cls) {
    final JsonElement element = map.get(key);
    return gson.fromJson(element, cls);
  }

  synchronized void save() {
    final File file = new File(configFile);
    logger.log("storing options to file: %s", file.getPath());
    try {
      if (!file.exists()) {
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
      }
      try (FileWriter out = new FileWriter(file)) {
        out.write(gson.toJson(map));
      }
    } catch(IOException e) {
      logger.log(e, "Failed to store options");
    }
  }

  private synchronized Map<String, JsonElement> load() {
    final HashMap<String, JsonElement> map = new HashMap<>();
    final File file = new File(configFile);
    logger.log("loading options from file: %s", file.getPath());
    try {
      if(file.exists()) {
        try (FileReader in = new FileReader(file)) {
          final JsonParser parser = new JsonParser();
          final JsonObject jsonObject = parser.parse(in).getAsJsonObject();
          for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
          }
        }
      }
    } catch(Throwable e) {
      logger.log(e, "Failed to load options");
    }
    return map;
  }
}
