package by.babanin.todo.ui;

import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.springframework.stereotype.Component;

import by.babanin.ext.message.Translator;
import by.babanin.todo.ui.translat.AppTranslateCode;

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
        fileMenu = new JMenu(Translator.toLocale(AppTranslateCode.MAIN_MENU_FILE));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(actionMap.get("showSettingsDialogAction"));
        fileMenu.addSeparator();
        fileMenu.add(actionMap.get("exitAction"));
    }

    private void initializeHelpMenu() {
        helpMenu = new JMenu(Translator.toLocale(AppTranslateCode.MAIN_MENU_HELP));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(actionMap.get("showAboutAction"));
    }

    private void placeComponents() {
        add(fileMenu);
        add(helpMenu);
    }
}
