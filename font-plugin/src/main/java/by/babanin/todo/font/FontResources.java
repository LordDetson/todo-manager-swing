package by.babanin.todo.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import by.babanin.todo.font.exception.FontResourcesException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FontResources {

    private static final String TTF = "ttf";
    private static final String FONTS_PATH = "assets/fonts/";
    private static final String STATIC_RUBIK_PATH = "rubik/static/";
    private static final List<String> STATIC_RUBIK_FILE_NAMES = List.of(
            "Rubik-Black",
            "Rubik-BlackItalic",
            "Rubik-Bold",
            "Rubik-BoldItalic",
            "Rubik-ExtraBold",
            "Rubik-ExtraBoldItalic",
            "Rubik-Italic",
            "Rubik-Light",
            "Rubik-LightItalic",
            "Rubik-Medium",
            "Rubik-MediumItalic",
            "Rubik-Regular",
            "Rubik-SemiBold",
            "Rubik-SemiBoldItalic"
    );

    public static void registerFonts() {
        String rubikPath = FONTS_PATH + STATIC_RUBIK_PATH;
        STATIC_RUBIK_FILE_NAMES.forEach(fontName -> registerFont(rubikPath, fontName));
    }

    public static void registerFont(String fontsPath, String name) {
        try(InputStream productSansBlackFontStream = FontResources.class.getResourceAsStream("/" + fontsPath + name + "." + TTF)) {
            if(productSansBlackFontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, productSansBlackFontStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            }
        }
        catch(IOException | FontFormatException e) {
            throw new FontResourcesException(e);
        }
    }
}
