package by.babanin.todo.model;

@FunctionalInterface
public interface Persistent<I> {

    I getId();
}
