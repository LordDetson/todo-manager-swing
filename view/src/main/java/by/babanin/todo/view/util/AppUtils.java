package by.babanin.todo.view.util;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import by.babanin.todo.image.IconResources;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.translat.AppTranslator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppUtils {

    public static final int DEFAULT_MENU_ICON_SIZE = 12;

    public static TableColumn createTableColumn(ReportField field, int modelIndex) {
        TableColumn column = new TableColumn(modelIndex);
        column.setIdentifier(field.getName());
        column.setHeaderValue(AppTranslator.getFieldCaption(field));
        return column;
    }

    public static Icon getMenuIcon(String name) {
        FlatSVGIcon icon = IconResources.getIcon(name, DEFAULT_MENU_ICON_SIZE);
        ColorFilter colorFilter = new ColorFilter();
        colorFilter.add(Color.BLACK, UIManager.getDefaults().getColor("Button.foreground"));
        icon.setColorFilter(colorFilter);
        return icon;
    }
}
