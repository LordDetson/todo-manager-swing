package by.babanin.todo.view;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import jakarta.annotation.PostConstruct;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class MainMenuBar extends JMenuBar {

    private boolean initialized;
    private JMenu fileMenu;
    private JMenu helpMenu;

    @PostConstruct
    public void initialize() {
        if(!initialized) {
            createUiComponents();
            placeComponents();
            initialized = true;
        }
        else {
            throw new ViewException("It's already initialized");
        }
    }

    private void createUiComponents() {
        initializeFileMenu();
        initializeHelpMenu();
    }

    private void initializeFileMenu() {
        fileMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_FILE));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(getExitAction());
    }

    @Lookup("exitAction")
    protected abstract Action getExitAction();

    private void initializeHelpMenu() {
        helpMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_HELP));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(getShowAboutAction());
    }

    @Lookup("showAboutAction")
    protected abstract Action getShowAboutAction();

    private void placeComponents() {
        add(fileMenu);
        add(helpMenu);
    }
}
