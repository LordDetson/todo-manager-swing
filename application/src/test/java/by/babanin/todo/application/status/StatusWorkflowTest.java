package by.babanin.todo.application.status;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import by.babanin.todo.application.config.EmptyConfiguration;
import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

@SpringBootTest(classes = EmptyConfiguration.class)
class StatusWorkflowTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void workflow() {
        Todo todo = Todo.builder()
                .title("Test")
                .creationDate(LocalDate.now())
                .status(Status.OPEN)
                .plannedDate(LocalDate.now())
                .build();

        StatusWorkflow openStatusWorkflow = StatusWorkflow.get(todo);
        Todo todoWithInProgressStatus = openStatusWorkflow.goNextStatus();

        assertAll(
                () -> assertEquals(Status.IN_PROGRESS, openStatusWorkflow.getNextStatus()),
                () -> assertFalse(openStatusWorkflow.isFinalStatus()),
                () -> assertEquals(Status.IN_PROGRESS, todoWithInProgressStatus.getStatus())
        );

        StatusWorkflow inProgressStatusWorkflow = StatusWorkflow.get(todoWithInProgressStatus);
        Todo todoWithClosedStatus = inProgressStatusWorkflow.goNextStatus();

        assertAll(
                () -> assertEquals(Status.CLOSED, inProgressStatusWorkflow.getNextStatus()),
                () -> assertFalse(inProgressStatusWorkflow.isFinalStatus()),
                () -> assertEquals(Status.CLOSED, todoWithClosedStatus.getStatus())
        );

        StatusWorkflow closedStatusWorkflow = StatusWorkflow.get(todoWithClosedStatus);

        assertAll(
                () -> assertThrows(ApplicationException.class, closedStatusWorkflow::goNextStatus),
                () -> assertThrows(ApplicationException.class, closedStatusWorkflow::getNextStatus),
                () -> assertTrue(closedStatusWorkflow.isFinalStatus())
        );
    }

    @Test
    void getWithEmptyTodo() {
        assertThrows(ApplicationException.class, () -> StatusWorkflow.get(null));
    }
}