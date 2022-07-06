package by.babanin.todo.view.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import by.babanin.todo.view.exception.ResourceException;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class GUIUtils {

    private static final int LARGE_FRAME_SCALE = 85;
    private static final int HALF_FRAME_SCALE = 50;
    private static final int SMALL_FRAME_SCALE = 35;
    private static final double SCALE_BASE = 100;

    public static Dimension getFullScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static Dimension getLargeFrameSize() {
        return getScaledMainFrameSize(LARGE_FRAME_SCALE);
    }

    public static Dimension getHalfFrameSize() {
        return getScaledMainFrameSize(HALF_FRAME_SCALE);
    }

    public static Dimension getSmallFrameSize() {
        return getScaledMainFrameSize(SMALL_FRAME_SCALE);
    }

    public static Dimension getScaledMainFrameSize(int scale) {
        return getScaledMainFrameSize(scale, scale);
    }

    public static Dimension getScaledMainFrameSize(int widthScale, int heightScale) {
        Dimension fullScreenSize = getFullScreenSize();
        return scaleDimension(fullScreenSize, widthScale, heightScale);
    }

    private static Dimension scaleDimension(Dimension dimension, int widthScale, int heightScale) {
        dimension.width = (int) (dimension.width * widthScale / SCALE_BASE);
        dimension.height = (int) (dimension.height * heightScale / SCALE_BASE);
        return dimension;
    }

    public static String buildResourcePath(String path, String name, String extension) {
        StringBuilder resourcePathBuilder = new StringBuilder();
        if(path != null && !path.isBlank()) {
            resourcePathBuilder.append(path);
        }
        if(name != null && !name.isBlank()) {
            resourcePathBuilder.append(name);
        }
        else {
            throw new ResourceException("name can't be empty");
        }
        if(extension != null && !extension.isBlank()) {
            resourcePathBuilder.append(".").append(extension);
        }
        return resourcePathBuilder.toString();
    }
}
