package by.babanin.todo.image;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.formdev.flatlaf.extras.FlatSVGIcon;

class IconResourcesTest {

    @Test
    void getIcon() {
        String iconName = "test";
        int iconSize = 16;

        FlatSVGIcon icon = IconResources.getIcon(iconName, iconSize);

        assertAll(
                () -> assertEquals(icon.getName(), IconResources.ICONS_PATH + iconName + "." + IconResources.SVG),
                () -> assertEquals(icon.getIconWidth(), iconSize),
                () -> assertEquals(icon.getIconHeight(), iconSize)
        );
    }

    @Test
    void getIconFromSubDirectory() {
        String subDirectory = "subdir/";
        String iconName = "test";
        int width = 16;
        int height = 32;

        FlatSVGIcon icon = IconResources.getIcon(subDirectory, iconName, width, height);

        assertAll(
                () -> assertEquals(icon.getName(), IconResources.ICONS_PATH + subDirectory + iconName + "." + IconResources.SVG),
                () -> assertEquals(icon.getIconWidth(), width),
                () -> assertEquals(icon.getIconHeight(), height)
        );
    }

    @Test
    void getIconFromEmptySubDirectory() {
        String subDirectory = "";
        String iconName = "test";
        int width = 16;
        int height = 32;

        FlatSVGIcon icon = IconResources.getIcon(subDirectory, iconName, width, height);

        assertAll(
                () -> assertEquals(icon.getName(), IconResources.ICONS_PATH + iconName + "." + IconResources.SVG),
                () -> assertEquals(icon.getIconWidth(), width),
                () -> assertEquals(icon.getIconHeight(), height)
        );
    }
}