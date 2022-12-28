package by.babanin.todo.view;

import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.font.FontResources;
import by.babanin.todo.image.IconResources;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.about.AboutDialog;
import by.babanin.todo.view.about.AboutInfo;
import by.babanin.todo.view.component.custom.UICustomizer;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.ServiceHolder;

@Component
public class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private static final int DEFAULT_ICON_SIZE = 32;
    private final ResourceBundleMessageSource messageSource;
    private final AboutInfo aboutInfo;
    private final PriorityService priorityService;
    private final TodoService todoService;

    public UILauncher(ResourceBundleMessageSource messageSource, AboutInfo aboutInfo, PriorityService priorityService,
            TodoService todoService) {
        this.messageSource = messageSource;
        this.aboutInfo = aboutInfo;
        this.priorityService = priorityService;
        this.todoService = todoService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Translator.setMessageSource(messageSource);
        AboutDialog.setAboutInfo(aboutInfo);
        ServiceHolder.setPriorityService(priorityService);
        ServiceHolder.setTodoService(todoService);
        ComponentRepresentation.initializeComponentRepresentationMap();
        FontResources.registerFonts();

        FlatLaf.registerCustomDefaultsSource( "themes" );
        FlatDarculaLaf.setup();
        UICustomizer.customize();
        if(SystemInfo.isMacOS) {
            setupPropertiesForMacOs();
        }
        EventQueue.invokeLater(this::showMainFrame);
    }

    private void showMainFrame() {
        JFrame mainFrame = new JFrame();
        GUIUtils.setMainWindow(mainFrame);
        mainFrame.setJMenuBar(new MainMenuBar());

        TodoPanel todoPanel = new TodoPanel();
        todoPanel.load();
        mainFrame.setContentPane(todoPanel);

        mainFrame.setSize(GUIUtils.getLargeFrameSize());
        mainFrame.setMinimumSize(GUIUtils.getHalfFrameSize());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setTitle(aboutInfo.getProduct().getName());
        mainFrame.setIconImage(IconResources.getIcon("transparent_check_hexagon", DEFAULT_ICON_SIZE).getImage());
        mainFrame.setVisible(true);
    }

    private void setupPropertiesForMacOs() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", aboutInfo.getProduct().getName());
        System.setProperty("apple.awt.application.appearance", "system");
        Desktop desktop = Desktop.getDesktop();
        if(desktop.isSupported(Desktop.Action.APP_ABOUT)) {
            desktop.setAboutHandler(e -> AboutDialog.show());
        }
        if(desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
            desktop.setQuitHandler((e, response) -> response.performQuit());
        }
    }
}
