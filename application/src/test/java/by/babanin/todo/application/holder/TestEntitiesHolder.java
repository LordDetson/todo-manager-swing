package by.babanin.todo.application.holder;

import java.util.Collection;
import java.util.List;

import by.babanin.todo.model.Persistent;

public interface TestEntitiesHolder<E extends Persistent<I> , I> {

    void setEntities(Collection<E> entities);

    List<E> getEntities();

    void setFakeEntities(Collection<E> fakeEntities);

    List<E> getFakeEntities();
}
