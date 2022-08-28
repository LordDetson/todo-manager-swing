package by.babanin.todo.application;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories
@EntityScan("by.babanin.todo.model")
@ComponentScan(basePackages = "by.babanin.todo.application")
public class TestApplicationConfiguration {

}
