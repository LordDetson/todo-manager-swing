package by.babanin.todo.view.settings;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import by.babanin.todo.view.component.table.adjustment.TableColumnAdjustment;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class TableColumnAdjustmentView extends AbstractSettingView<TableColumnAdjustment> {

    public TableColumnAdjustmentView(TableColumnAdjustment tableColumnAdjustment, SettingViewType settingViewType) {
        super(tableColumnAdjustment, settingViewType);
    }

    @Override
    protected JPanel createContentPanel(TableColumnAdjustment setting, TableColumnAdjustment accumulator, SettingViewType settingViewType) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createCheckBox(
                TranslateCode.SETTINGS_COLUMNS_HEADER_INCLUDED,
                setting.isColumnHeaderIncluded(),
                accumulator::setColumnHeaderIncluded));
        panel.add(createCheckBox(
                TranslateCode.SETTINGS_COLUMNS_CONTENT_INCLUDED,
                setting.isColumnContentIncluded(),
                accumulator::setColumnContentIncluded));
        panel.add(createCheckBox(
                TranslateCode.SETTINGS_ONLY_ADJUST_LARGER,
                setting.isOnlyAdjustLarger(),
                accumulator::setOnlyAdjustLarger));
        panel.add(createCheckBox(
                TranslateCode.SETTINGS_DYNAMIC_ADJUSTMENT,
                setting.isDynamicAdjustment(),
                accumulator::setDynamicAdjustment));
        return panel;
    }

    private JCheckBox createCheckBox(String translateCode, boolean selected, Consumer<Boolean> setter) {
        JCheckBox checkBox = new JCheckBox(new AbstractAction(Translator.toLocale(translateCode)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                setter.accept(((AbstractButton) e.getSource()).isSelected());
                fireChange();
            }
        });
        checkBox.setSelected(selected);
        return checkBox;
    }
}
