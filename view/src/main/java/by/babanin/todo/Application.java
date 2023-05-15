package by.babanin.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;

import by.babanin.ext.component.logger.LogMessageType;
import by.babanin.ext.component.util.IconRegister;
import by.babanin.ext.representation.RepresentationRegister;
import by.babanin.todo.font.FontResources;
import by.babanin.todo.image.IconResources;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.dto.ToDoInfo;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("spring.config.additional-location", "classpath:view.properties");
        FontResources.registerFonts();
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatDarculaLaf.setup();
        RepresentationRegister.register(PriorityInfo.class);
        RepresentationRegister.register(ToDoInfo.class);
        IconRegister.registerLogMessageTypeIcon(LogMessageType.ERROR,
                IconResources.getIcon("error", IconRegister.LOG_MESSAGE_TYPE_ICON_SIZE));
        IconRegister.registerLogMessageTypeIcon(LogMessageType.WARNING,
                IconResources.getIcon("warning", IconRegister.LOG_MESSAGE_TYPE_ICON_SIZE));
        IconRegister.registerLogMessageTypeIcon(LogMessageType.INFORMATION,
                IconResources.getIcon("information", IconRegister.LOG_MESSAGE_TYPE_ICON_SIZE));

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setHeadless(false);
        springApplication.run(args);
    }
}
