package by.babanin.todo.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.swing.UIManager;

import by.babanin.todo.font.exception.FontResourcesException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FontResources {

    public static void registerAdditionalFonts(String fontsPath) {
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-Black.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-BlackItalic.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-Bold.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-BoldItalic.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-ExtraBold.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-ExtraBoldItalic.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-Italic.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-Light.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-LightItalic.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-Medium.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-MediumItalic.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-Regular.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-SemiBold.ttf");
        registerAdditionalFont(fontsPath, "rubik/static/Rubik-SemiBoldItalic.ttf");
    }

    private static void registerAdditionalFont(String fontsPath, String name) {
        try(InputStream productSansBlackFontStream = FontResources.class.getResourceAsStream("/" + fontsPath + name)) {
            if(productSansBlackFontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, productSansBlackFontStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            }
        }
        catch(IOException | FontFormatException e) {
            throw new FontResourcesException(e);
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
            throw new FontResourcesException(fontName + " doesn't exist");
        }
    }
}
