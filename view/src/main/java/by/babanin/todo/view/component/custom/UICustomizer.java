package by.babanin.todo.view.component.custom;

import java.awt.event.InputEvent;

import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentInputMapUIResource;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UICustomizer {

    public static final String MNEMONIC_MODIFIERS_KEY = "mnemonicModifiers";
    private static final int[] DEFAULT_MNEMONIC_MODIFIERS = new int[] {
            InputEvent.ALT_DOWN_MASK,
            InputEvent.ALT_GRAPH_DOWN_MASK
    };

    public static void customize() {
        UIManager.put("ButtonUI", "by.babanin.todo.view.component.custom.CustomFlatButtonUI");
        UIManager.put("ToggleButtonUI", "by.babanin.todo.view.component.custom.CustomFlatToggleButtonUI");
        UIManager.put(MNEMONIC_MODIFIERS_KEY, getMnemonicModifiers());
        UIManager.put("Menu.shortcutKeys", getMnemonicModifiers());
    }

    public static int[] getMnemonicModifiers() {
        return DEFAULT_MNEMONIC_MODIFIERS;
    }

    public static void updateMnemonicBinding(AbstractButton button) {
        int m = button.getMnemonic();
        InputMap map = SwingUtilities.getUIInputMap(button, JComponent.WHEN_IN_FOCUSED_WINDOW);
        if(m != 0) {
            if(map == null) {
                map = new ComponentInputMapUIResource(button);
                SwingUtilities.replaceUIInputMap(button, JComponent.WHEN_IN_FOCUSED_WINDOW, map);
            }
            map.clear();
            int[] mnemonicModifiers = (int[]) UIManager.get(MNEMONIC_MODIFIERS_KEY);
            for(int modifiers : mnemonicModifiers) {
                map.put(KeyStroke.getKeyStroke(m, modifiers, false), "pressed");
                map.put(KeyStroke.getKeyStroke(m, modifiers, true), "released");
            }
            map.put(KeyStroke.getKeyStroke(m, 0, true), "released");
        }
        else {
            if(map != null) {
                map.clear();
            }
        }
    }
}
