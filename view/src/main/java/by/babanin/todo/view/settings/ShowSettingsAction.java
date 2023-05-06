package by.babanin.todo.view.settings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Icon;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import by.babanin.todo.view.component.action.Action;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
public abstract class ShowSettingsAction extends Action {

    protected ShowSettingsAction(Icon settingsIcon) {
        setName(Translator.toLocale(TranslateCode.MAIN_MENU_SETTINGS));
        setSmallIcon(settingsIcon);
        setLargeIcon(settingsIcon);
        setMnemonic(KeyEvent.VK_S);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createSettingsDialog().setVisible(true);
    }

    @Lookup
    protected abstract SettingsDialog createSettingsDialog();
}
