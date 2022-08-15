package by.babanin.todo.view.component;

import javax.swing.table.DefaultTableColumnModel;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.util.GUIUtils;

public class CustomTableColumnModel<C> extends DefaultTableColumnModel {

    public CustomTableColumnModel(Class<C> componentClass, CrudStyle crudStyle) {
        ComponentRepresentation<C> representation = ComponentRepresentation.get(componentClass);
        representation.getFields().stream()
                .filter(field -> !crudStyle.getExcludedFieldFromTable().contains(field.getName()))
                .map(GUIUtils::createTableColumn)
                .forEachOrdered(this::addColumn);
    }
}
