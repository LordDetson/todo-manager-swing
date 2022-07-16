package by.babanin.todo.model;

public interface Persistent<I> {

    I getId();

    void setId(I id);
}
