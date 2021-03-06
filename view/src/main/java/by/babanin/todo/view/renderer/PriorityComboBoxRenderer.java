package by.babanin.todo.view.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import by.babanin.todo.model.Priority;

public class PriorityComboBoxRenderer extends BasicComboBoxRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof Priority priority) {
            value = priority.getName();
        }
        if(value == null) {
            value = "None";
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
