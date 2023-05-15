package by.babanin.todo.integration;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import by.babanin.ext.component.util.IconProvider;
import by.babanin.todo.image.IconResources;

public class IconProviderImpl implements IconProvider {

    @Override
    public Icon get(String name, int iconSize) {
        FlatSVGIcon icon = IconResources.getIcon(name, iconSize);
        ColorFilter colorFilter = new ColorFilter();
        colorFilter.add(Color.BLACK, UIManager.getDefaults().getColor("Button.foreground"));
        icon.setColorFilter(colorFilter);
        return icon;
    }
}
