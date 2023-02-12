package by.babanin.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;

import by.babanin.todo.font.FontResources;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.custom.UICustomizer;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("spring.config.additional-location", "classpath:view.properties");
        ComponentRepresentation.initializeComponentRepresentationMap();
        FontResources.registerFonts();
        FlatLaf.registerCustomDefaultsSource( "themes" );
        FlatDarculaLaf.setup();
        UICustomizer.customize();

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setHeadless(false);
        springApplication.run(args);
    }
}
