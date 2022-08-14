package by.babanin.todo.view.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.swing.UIManager;

import by.babanin.todo.view.exception.ViewException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FontResources {

    public static void registerAdditionalFonts(String fontsPath) {
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-Black.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-BlackItalic.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-Bold.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-BoldItalic.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-ExtraBold.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-ExtraBoldItalic.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-Italic.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-Light.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-LightItalic.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-Medium.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-MediumItalic.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-Regular.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-SemiBold.ttf");
        registerAdditionalFont(fontsPath, "Rubik/static/Rubik-SemiBoldItalic.ttf");
    }

    private static void registerAdditionalFont(String fontsPath, String name) {
        try(InputStream productSansBlackFontStream = FontResources.class.getResourceAsStream("/" + fontsPath + name)) {
            if(productSansBlackFontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, productSansBlackFontStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            }
        }
        catch(IOException | FontFormatException e) {
            throw new ViewException(e);
        }
    }

    public static void applyFontByDefault(Font font) {
        String fontName = font.getName();
        if(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains(fontName)) {
            for(Object key : UIManager.getDefaults().keySet()) {
                Object value = UIManager.get(key);
                if(value instanceof Font) {
                    UIManager.put(key, font);
                }
            }
        }
        else {
            throw new ViewException(fontName + " doesn't exist");
        }
    }
}
