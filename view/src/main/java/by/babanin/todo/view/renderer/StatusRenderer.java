package by.babanin.todo.view.renderer;

import javax.swing.table.DefaultTableCellRenderer;

import by.babanin.todo.model.Status;
import by.babanin.todo.view.translat.Translator;

public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if(value instanceof Status status) {
            setText(Translator.getStatusCaption(status));
        }
        else {
            super.setValue(value);
        }
    }
}
