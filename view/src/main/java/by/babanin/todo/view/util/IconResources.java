package by.babanin.todo.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import by.babanin.todo.view.exception.ResourceException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IconResources {
    public static final String PNG = "png";

    private static String iconPath;

    public static void setIconPath(String iconPath) {
        IconResources.iconPath = iconPath;
    }

    public static Icon getIcon(String name) {
        return getIcon(name, PNG);
    }

    public static Icon getIcon(String name, String extension) {
        return getIcon(null, name, extension);
    }

    public static Icon getIcon(String path, String name, String extension) {
        if(path == null || path.isBlank()) {
            path = "";
        }
        path = iconPath + path;
        InputStream resourceAsStream = getResourceAsStream(path, name, extension);
        try {
            BufferedImage image = ImageIO.read(resourceAsStream);
            return new ImageIcon(image);
        }
        catch(IOException e) {
            throw new ResourceException(e);
        }
    }

    public static InputStream getResourceAsStream(String path, String name, String extension) {
        InputStream imageStream = IconResources.class.getResourceAsStream(GUIUtils.buildResourcePath(path, name, extension));
        return Objects.requireNonNull(imageStream);
    }
}
