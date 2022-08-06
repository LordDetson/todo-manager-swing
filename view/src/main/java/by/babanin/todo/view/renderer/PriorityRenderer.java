package by.babanin.todo.view.renderer;

import javax.swing.table.DefaultTableCellRenderer;

import by.babanin.todo.model.Priority;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class PriorityRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if(value instanceof Priority priority) {
            setText(priority.getName());
        }
        else if(value == null) {
            setText(Translator.toLocale(TranslateCode.PRIORITY_UNPRIORITIZED));
        }
        else {
            super.setValue(value);
        }
    }
}
