package by.babanin.todo.ui.renderer;

import javax.swing.table.DefaultTableCellRenderer;

import by.babanin.todo.model.Status;
import by.babanin.todo.ui.translat.AppTranslator;

public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if(value instanceof Status status) {
            setText(AppTranslator.getStatusCaption(status));
        }
        else {
            super.setValue(value);
        }
    }
}
