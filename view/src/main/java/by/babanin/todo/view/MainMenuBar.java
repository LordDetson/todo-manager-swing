package by.babanin.todo.view;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import by.babanin.todo.view.about.ShowAboutAction;
import by.babanin.todo.view.component.MenuAction;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class MainMenuBar extends JMenuBar {

    private final transient BeanFactory beanFactory;

    private JMenu fileMenu;
    private JMenu helpMenu;

    public MainMenuBar(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
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

        fileMenu.add(createExitAction());
    }

    private Action createExitAction() {
        return new MenuAction(
                Translator.toLocale(TranslateCode.MAIN_MENU_EXIT),
                beanFactory.getBean("exitIcon", FlatSVGIcon.class),
                KeyEvent.VK_X,
                () -> System.exit(0)
        );
    }

    private void initializeHelpMenu() {
        helpMenu = new JMenu(Translator.toLocale(TranslateCode.MAIN_MENU_HELP));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(beanFactory.getBean(ShowAboutAction.class));
    }

    private void placeComponents() {
        add(fileMenu);
        add(helpMenu);
    }
}
