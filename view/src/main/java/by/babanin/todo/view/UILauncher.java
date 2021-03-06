package by.babanin.todo.view;

import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.FlatDarculaLaf;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.IconResources;
import by.babanin.todo.view.util.ServiceHolder;

@Component
public class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final ResourceBundleMessageSource messageSource;
    private final PriorityService priorityService;
    private final TodoService todoService;
    private final ExecutorService executorService;

    @Value("${application.resource.icons.path:/assets/icons/}")
    private String iconsPath;

    public UILauncher(ResourceBundleMessageSource messageSource, PriorityService priorityService, TodoService todoService,
            ExecutorService executorService) {
        this.messageSource = messageSource;
        this.priorityService = priorityService;
        this.todoService = todoService;
        this.executorService = executorService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Translator.setMessageSource(messageSource);
        ServiceHolder.setPriorityService(priorityService);
        ServiceHolder.setTodoService(todoService);
        IconResources.setIconsPath(iconsPath);
        TaskManager.setExecutorService(executorService);
        ComponentRepresentation.initializeComponentRepresentationMap();

        FlatDarculaLaf.setup();
        EventQueue.invokeLater(() -> {

            TodoCrudTablePanel todoPanel = new TodoCrudTablePanel();
            todoPanel.load();

            JFrame mainFrame = new JFrame();
            mainFrame.setContentPane(todoPanel);
            mainFrame.setSize(GUIUtils.getLargeFrameSize());
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setTitle(Translator.toLocale(TranslateCode.TODO_FRAME_TITLE));
            mainFrame.setVisible(true);

            GUIUtils.setMainWindow(mainFrame);
        });
    }
}
