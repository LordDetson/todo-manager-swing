package by.babanin.todo.image;

import java.awt.Image;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IconResources {

    public static final String SVG = "svg";

    public static final String ICONS_PATH = "assets/icons/";

    public static FlatSVGIcon getIcon(String name, int size) {
        return getIcon(null, name, size, size);
    }

    public static FlatSVGIcon getIcon(String path, String name, int width, int height) {
        return new FlatSVGIcon(buildIconPath(path, name), width, height);
    }

    public static Image getImage(String name) {
        return getImage(null, name);
    }

    public static Image getImage(String path, String name) {
        return FlatSVGUtils.createWindowIconImages("/" + buildIconPath(path, name)).get(0);
    }

    public static String buildIconPath(String path, String name) {
        if(path == null || path.isBlank()) {
            path = "";
        }
        path = ICONS_PATH + path;
        return ResourceUtils.buildResourcePath(path, name, SVG);
    }
}
