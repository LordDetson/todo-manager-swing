package by.babanin.todo.font;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import javax.swing.UIManager;

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

    @Test
    void applyFontByDefault() {
        String familyName = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()[0];
        Font font = new Font(familyName, Font.PLAIN, 8);
        FontResources.applyFontByDefault(font);

        boolean allComponentsHaveAppliedFont = true;
        for(Object key : UIManager.getDefaults().keySet()) {
            Object value = UIManager.get(key);
            if(value instanceof Font) {
                if(UIManager.get(key) != font) {
                    allComponentsHaveAppliedFont = false;
                }
            }
        }
        assertTrue(allComponentsHaveAppliedFont);
    }

    @Test
    void isRegisteredFont() {
        String familyName = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()[0];
        assertTrue(FontResources.isRegisteredFont(familyName));
    }
}