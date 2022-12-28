package by.babanin.todo.font;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FontResourcesTest {

    @Test
    void registerFonts() {
        FontResources.registerFonts();
        assertTrue(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains("Rubik"));

    }

    @Test
    void registerFont() {
        String path = "assets/fonts/";
        String name = "Rubik-Regular";
        FontResources.registerFont(path, name);
        assertTrue(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains("Rubik"));
    }
}