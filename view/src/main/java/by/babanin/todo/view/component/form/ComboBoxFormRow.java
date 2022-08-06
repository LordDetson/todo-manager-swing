package by.babanin.todo.view.component.form;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import by.babanin.todo.representation.ReportField;

public class ComboBoxFormRow<T> extends FormRow<T> {

    private final JComboBox<T> comboBox;
    private boolean allowNone;

    public ComboBoxFormRow(ReportField field) {
        super(field);
        this.comboBox = new JComboBox<>();
        comboBox.setMaximumSize(new Dimension(comboBox.getMaximumSize().width, comboBox.getPreferredSize().height));
        comboBox.addItemListener(event -> stateChanged());
    }

    @Override
    public JComponent getInputComponent() {
        return comboBox;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T) comboBox.getSelectedItem();
    }

    @Override
    public void setValue(T value) {
        comboBox.setSelectedItem(value);
    }

    public void setRenderer(ListCellRenderer<? super T> renderer) {
        comboBox.setRenderer(renderer);
    }

    public void setAllowNone(boolean allowNone) {
        this.allowNone = allowNone;
    }

    public void setItems(List<T> items) {
        comboBox.removeAllItems();
        if(allowNone) {
            comboBox.addItem(null);
        }
        items.forEach(comboBox::addItem);
    }
}
