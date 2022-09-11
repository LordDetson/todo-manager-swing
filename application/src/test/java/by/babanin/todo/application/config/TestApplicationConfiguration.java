package by.babanin.todo.application.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("by.babanin.todo.application.repository")
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EntityScan("by.babanin.todo.model")
@ComponentScan(basePackages = "by.babanin.todo.application")
public class TestApplicationConfiguration {

}
