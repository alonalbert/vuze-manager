package com.alon.vuze.vuzemanager.resources;

import com.alon.vuze.vuzemanager.logger.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;

@Singleton
public class ImageRepository {
  public enum ImageResource {
    ADD("add.png"),
    REMOVE("remove.png"),;

    private final String filename;

    ImageResource(String filename) {
      this.filename = filename;
    }
  }

  @Inject
  private Logger logger;

  private final Map<ImageResource, Image> images = new HashMap<>();


  @Inject
  public ImageRepository() {
  }

  public Image getImage(Display display, ImageResource imageResource) {
    Image image = images.get(imageResource);

    if (image == null) {
      final String base = ImageRepository.class.getPackage().getName().replace(".", "/");
      final String path = base + "/" + imageResource.filename;
      final InputStream in = ImageRepository.class.getClassLoader().getResourceAsStream(path);
      if (in != null) {
        image = new Image(display, in);
        images.put(imageResource, image);
      } else {
        logger.log("ImageRepository:getImage:: Resource not found: %s", path);
      }
    }
    return image;
  }

  public void unLoadImages() {
    images.values().forEach(Resource::dispose);
  }
}
