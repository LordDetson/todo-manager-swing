package by.babanin.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "by.babanin.todo.datagenerator",
        "by.babanin.todo.application",
        "by.babanin.todo.model"
})
@EnableJpaRepositories
public class DataGenerationLauncher {

    public static void main(String[] args) {
        SpringApplication.run(DataGenerationLauncher.class, args);
    }
}
