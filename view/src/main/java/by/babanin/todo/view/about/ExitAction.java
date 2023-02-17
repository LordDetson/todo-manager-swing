package by.babanin.todo.view.about;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExitAction extends AbstractAction {

    public ExitAction(Icon exitIcon) {
        super(Translator.toLocale(TranslateCode.MAIN_MENU_EXIT), exitIcon);
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
