package by.babanin.todo.view.renderer;

import javax.swing.table.DefaultTableCellRenderer;

import by.babanin.todo.model.Priority;

public class PriorityRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if(value instanceof Priority priority) {
            setText(priority.getName());
        }
        else {
            super.setValue(value);
        }
    }
}
