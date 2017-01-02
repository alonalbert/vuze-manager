package com.alon.vuze.vuzemanager.resources;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageRepository {
  public enum ImageResource {
    ADD("add.png"),
    REMOVE("remove.png"),;

    private final String filename;

    ImageResource(String filename) {
      this.filename = filename;
    }
  }

  private static final Map<ImageResource, Image> images = new HashMap<>();

  public static Image getImage(Display display, ImageResource imageResource) {
    Image image = images.get(imageResource);

    if (image == null) {
      final String base = ImageRepository.class.getPackage().getName().replace(".", "/");
      final String path = base + "/" + imageResource.filename;
      final InputStream in = ImageRepository.class.getClassLoader().getResourceAsStream(path);
      if (in != null) {
        image = new Image(display, in);
        images.put(imageResource, image);
      } else {
        // TODO: 12/31/16 logger
        System.out.println("ImageRepository:getImage:: Resource not found: " + path);
      }
    }
    return image;
  }

  public static void unLoadImages() {
    for (Image im : images.values()) {
      im.dispose();
    }
  }
}
