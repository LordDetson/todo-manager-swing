package by.babanin.todo.view.settings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ShowSettingsAction extends AbstractAction {

    protected ShowSettingsAction(FlatSVGIcon settingsIcon) {
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
