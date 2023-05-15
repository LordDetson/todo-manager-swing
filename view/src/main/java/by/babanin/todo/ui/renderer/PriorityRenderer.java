package by.babanin.todo.ui.renderer;

import javax.swing.table.DefaultTableCellRenderer;

import by.babanin.ext.message.Translator;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.translat.AppTranslateCode;

public class PriorityRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if(value instanceof PriorityInfo priority) {
            setText(priority.getName());
        }
        else if(value == null) {
            setText(Translator.toLocale(AppTranslateCode.PRIORITY_UNPRIORITIZED));
        }
        else {
            super.setValue(value);
        }
    }
}
