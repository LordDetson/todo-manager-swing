package by.babanin.todo.task;

import java.util.concurrent.ExecutorService;

import by.babanin.todo.task.exception.TaskException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskManager {

    private static ExecutorService executorService;

    public static void setExecutorService(ExecutorService executorService) {
        if(TaskManager.executorService != null) {
            throw new TaskException("Executor service already exist");
        }
        TaskManager.executorService = executorService;
    }

    public static void run(Task<?> task) {
        executorService.execute(task);
    }
}
