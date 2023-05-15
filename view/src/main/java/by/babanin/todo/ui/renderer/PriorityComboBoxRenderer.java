package by.babanin.todo.ui.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import by.babanin.ext.message.Translator;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.translat.AppTranslateCode;

public class PriorityComboBoxRenderer extends BasicComboBoxRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof PriorityInfo priority) {
            value = priority.getName();
        }
        if(value == null) {
            value = Translator.toLocale(AppTranslateCode.PRIORITY_UNPRIORITIZED);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
