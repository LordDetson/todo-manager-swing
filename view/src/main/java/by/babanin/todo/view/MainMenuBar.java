package by.babanin.todo.view;

import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.springframework.stereotype.Component;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
public class MainMenuBar extends JMenuBar {

    private final Map<String, Action> actionMap;

    private JMenu fileMenu;
    private JMenu helpMenu;

    public MainMenuBar(Map<String, Action> actionMap) {
        this.actionMap = actionMap;
        createUiComponents();
        placeComponents();
    }

    private void createUiComponents() {
        initializeFileMenu();
        initializeHelpMenu();
    }

    private void initializeFileMenu() {
        fileMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_FILE));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(actionMap.get("showSettingsAction"));
        fileMenu.addSeparator();
        fileMenu.add(actionMap.get("exitAction"));
    }

    private void initializeHelpMenu() {
        helpMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_HELP));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(actionMap.get("showAboutAction"));
    }

    private void placeComponents() {
        add(fileMenu);
        add(helpMenu);
    }
}
