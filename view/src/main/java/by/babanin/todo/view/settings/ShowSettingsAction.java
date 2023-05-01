package by.babanin.todo.view.settings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
public abstract class ShowSettingsAction extends AbstractAction {

    protected ShowSettingsAction(Icon settingsIcon) {
        super(Translator.toLocale(TranslateCode.MAIN_MENU_SETTINGS), settingsIcon);
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createSettingsDialog().setVisible(true);
    }

    @Lookup
    protected abstract SettingsDialog createSettingsDialog();
}
