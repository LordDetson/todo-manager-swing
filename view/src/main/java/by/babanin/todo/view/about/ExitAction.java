package by.babanin.todo.view.about;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Icon;

import org.springframework.stereotype.Component;

import by.babanin.ext.component.action.Action;
import by.babanin.ext.message.Translator;
import by.babanin.todo.view.translat.AppTranslateCode;

@Component
public class ExitAction extends Action {

    public ExitAction(Icon exitIcon) {
        setId("exit");
        setName(Translator.toLocale(AppTranslateCode.MAIN_MENU_EXIT));
        setSmallIcon(exitIcon);
        setLargeIcon(exitIcon);
        setMnemonic(KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
