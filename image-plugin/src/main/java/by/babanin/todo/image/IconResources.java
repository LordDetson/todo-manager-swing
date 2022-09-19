package by.babanin.todo.image;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IconResources {

    public static final String SVG = "svg";

    public static final String ICONS_PATH = "assets/icons/";

    public static FlatSVGIcon getIcon(String name, int size) {
        return getIcon(null, name, size, size);
    }

    public static FlatSVGIcon getIcon(String path, String name, int width, int height) {
        if(path == null || path.isBlank()) {
            path = "";
        }
        path = ICONS_PATH + path;
        path = ResourceUtils.buildResourcePath(path, name, SVG);
        return new FlatSVGIcon(path, width, height);
    }
}
