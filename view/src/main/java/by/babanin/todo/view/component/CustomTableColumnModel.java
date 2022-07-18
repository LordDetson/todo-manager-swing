package by.babanin.todo.view.component;

import javax.swing.table.DefaultTableColumnModel;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.util.GUIUtils;

public class CustomTableColumnModel<C> extends DefaultTableColumnModel {

    public CustomTableColumnModel(Class<C> componentClass) {
        ComponentRepresentation<C> representation = ComponentRepresentation.get(componentClass);
        representation.getFields().stream()
                .map(GUIUtils::createTableColumn)
                .forEachOrdered(this::addColumn);
    }
}
