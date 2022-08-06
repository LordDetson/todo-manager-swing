package by.babanin.todo.datagenerator;

import java.time.LocalDate;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

@Component
public class DataGenerator implements ApplicationListener<ContextRefreshedEvent> {

    private final Environment environment;
    private final PriorityService priorityService;
    private final TodoService todoService;

    public DataGenerator(Environment environment, PriorityService priorityService, TodoService todoService) {
        this.environment = environment;
        this.priorityService = priorityService;
        this.todoService = todoService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        int iterations = isLarge() ? getIterations() : 1;
        todoService.deleteAll();
        priorityService.deleteAll();
        Priority high = priorityService.create("High");
        Priority normal = priorityService.create("Normal");
        Priority low = priorityService.create("Low");
        for(int i = 0; i < iterations; i++) {
            todoService.create("Email team for update", """
                        The project has changed the requirements for some features.
                        I need to write a letter about this to our team.
                        """,
                    high, LocalDate.now().plusDays(2));
            todoService.create("Update project plane", """
                        1. Send an email update to the team.
                        2. Call the design agency to finalize mockups.
                        3. Touch base with recruiters about the new role.
                        4. Meet with the engineering team.""",
                    normal, LocalDate.now().plusDays(7));
            Todo contactServiceCenter = todoService.create("Contact service center", """
                        Unable to restore access to my account.
                        Report a problem, get a list of troubleshooting steps.
                        """,
                    low, LocalDate.now().plusMonths(1));
            todoService.update(contactServiceCenter,
                    contactServiceCenter.getTitle(),
                    contactServiceCenter.getDescription(),
                    contactServiceCenter.getPriority(),
                    Status.IN_PROGRESS);
            Todo buyProductsInTheStore = todoService.create("Buy products in the store", """
                        10 eggs
                        Milk 1 liter
                        Cheese 300 grams
                        Chicken fillet
                        Apples
                        Candies
                        """,
                    null, LocalDate.now());
            todoService.update(buyProductsInTheStore,
                    buyProductsInTheStore.getTitle(),
                    buyProductsInTheStore.getDescription(),
                    buyProductsInTheStore.getPriority(),
                    Status.CLOSED);
        }
    }

    private boolean isLarge() {
        return environment.getProperty("data.generator.large", Boolean.class, Boolean.FALSE);
    }

    private int getIterations() {
        return environment.getProperty("data.generator.iterations", Integer.class, 1000);
    }
}
