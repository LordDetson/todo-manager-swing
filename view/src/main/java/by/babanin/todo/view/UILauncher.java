package by.babanin.todo.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.font.FontResources;
import by.babanin.todo.image.IconResources;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.custom.UICustomizer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.ServiceHolder;

@Component
public class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private static final int DEFAULT_ICON_SIZE = 32;
    private final ResourceBundleMessageSource messageSource;
    private final PriorityService priorityService;
    private final TodoService todoService;

    public UILauncher(ResourceBundleMessageSource messageSource, PriorityService priorityService, TodoService todoService) {
        this.messageSource = messageSource;
        this.priorityService = priorityService;
        this.todoService = todoService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Translator.setMessageSource(messageSource);
        ServiceHolder.setPriorityService(priorityService);
        ServiceHolder.setTodoService(todoService);
        ComponentRepresentation.initializeComponentRepresentationMap();
        FontResources.registerFonts();

        FlatLaf.registerCustomDefaultsSource( "themes" );
        FlatDarculaLaf.setup();
        UICustomizer.customize();
        EventQueue.invokeLater(this::showMainFrame);
    }

    private void showMainFrame() {
        JFrame mainFrame = new JFrame();
        GUIUtils.setMainWindow(mainFrame);

        TodoPanel todoPanel = new TodoPanel();
        todoPanel.load();
        mainFrame.setContentPane(todoPanel);

        mainFrame.setSize(GUIUtils.getLargeFrameSize());
        mainFrame.setMinimumSize(GUIUtils.getHalfFrameSize());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setTitle(Translator.toLocale(TranslateCode.TODO_FRAME_TITLE));
        mainFrame.setIconImage(IconResources.getIcon("transparent_check_hexagon", DEFAULT_ICON_SIZE).getImage());
        mainFrame.setVisible(true);
    }
}
