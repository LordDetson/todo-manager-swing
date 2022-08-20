package by.babanin.todo.view.component.custom;

import java.beans.PropertyChangeEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;

import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatToggleButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

public class CustomFlatToggleButtonUI extends FlatToggleButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return FlatUIUtils.canUseSharedUI(c)
                ? FlatUIUtils.createSharedUI(FlatButtonUI.class, () -> new CustomFlatToggleButtonUI(true))
                : new CustomFlatToggleButtonUI(false);
    }

    protected CustomFlatToggleButtonUI(boolean shared) {
        super(shared);
    }

    @Override
    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new CustomFlatButtonListener(b);
    }

    protected class CustomFlatButtonListener extends FlatButtonListener {

        protected CustomFlatButtonListener(AbstractButton b) {
            super(b);
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange(e);
            String prop = e.getPropertyName();
            if(prop.equals(AbstractButton.MNEMONIC_CHANGED_PROPERTY)) {
                UICustomizer.updateMnemonicBinding((AbstractButton) e.getSource());
            }
        }
    }
}
