package by.babanin.todo.task;

import java.util.function.Consumer;

@FunctionalInterface
public interface ExceptionListener extends Consumer<Exception> {

}
