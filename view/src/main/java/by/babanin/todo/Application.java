package by.babanin.todo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("spring.config.additional-location", "classpath:view.properties");
        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);
    }
}
