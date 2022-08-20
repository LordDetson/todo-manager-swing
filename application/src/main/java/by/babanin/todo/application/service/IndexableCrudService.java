package by.babanin.todo.application.service;

import java.util.List;

import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;

public interface IndexableCrudService<E extends Persistent<I> & Indexable, I> extends CrudService<E, I> {

    E insert(long position, E e);

    void swap(long position1, long position2);

    List<E> getSubList(long position1, long position2);
}
