package by.babanin.todo.task;

import java.util.function.Consumer;

@FunctionalInterface
public interface FinishListener<R> extends Consumer<R> {

}
