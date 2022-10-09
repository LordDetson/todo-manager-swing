package by.babanin.todo.view.component;

import javax.swing.table.DefaultTableColumnModel;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.GUIUtils;

public class CustomTableColumnModel<C> extends DefaultTableColumnModel {

    public CustomTableColumnModel(ComponentRepresentation<C> representation) {
        int modelIndex = 0;
        for(ReportField field : representation.getFields()) {
            addColumn(GUIUtils.createTableColumn(field, modelIndex++));
        }
    }
}
