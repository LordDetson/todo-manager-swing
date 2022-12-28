package by.babanin.todo.view;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import by.babanin.todo.image.IconResources;
import by.babanin.todo.view.about.AboutDialog;
import by.babanin.todo.view.component.MenuAction;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class MainMenuBar extends JMenuBar {

    private static final int DEFAULT_ICON_SIZE = 12;

    private JMenu fileMenu;
    private JMenu helpMenu;

    public MainMenuBar() {
        createUiComponents();
        placeComponents();
    }

    protected void createUiComponents() {
        initializeFileMenu();
        initializeHelpMenu();
    }

    private void initializeFileMenu() {
        fileMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_FILE));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(createExitAction());
    }

    private Action createExitAction() {
        return new MenuAction(
                Translator.toLocale(TranslateCode.MAIN_MENU_EXIT),
                getIcon("out_door"),
                KeyEvent.VK_X,
                () -> System.exit(0)
        );
    }

    private void initializeHelpMenu() {
        helpMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_HELP));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(createAboutAction());
    }

    private Action createAboutAction() {
        return new MenuAction(
                Translator.toLocale(TranslateCode.MAIN_MENU_ABOUT),
                getIcon("i"),
                KeyEvent.VK_A,
                AboutDialog::show
        );
    }

    protected void placeComponents() {
        add(fileMenu);
        add(helpMenu);
    }

    private Icon getIcon(String name) {
        FlatSVGIcon icon = IconResources.getIcon(name, DEFAULT_ICON_SIZE);
        ColorFilter colorFilter = new ColorFilter();
        colorFilter.add(Color.BLACK, UIManager.getDefaults().getColor("Button.foreground"));
        icon.setColorFilter(colorFilter);
        return icon;
    }
}
