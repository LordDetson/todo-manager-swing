package by.babanin.todo.view.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import by.babanin.todo.model.Status;
import by.babanin.todo.view.translat.Translator;

public class StatusComboBoxRenderer extends BasicComboBoxRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof Status status) {
            value = Translator.getStatusCaption(status);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
