package by.babanin.todo.application.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import by.babanin.todo.application.exception.TestException;
import by.babanin.todo.model.Persistent;

public abstract class AbstractTestEntitiesHolder<E extends Persistent<I>, I> implements TestEntitiesHolder<E, I> {

    private final List<E> entities = new ArrayList<>();
    private final List<E> fakeEntities = new ArrayList<>();
    private final Comparator<I> idComparator;
    private final BiFunction<I, Integer, I> fakeIdGenerator;

    protected AbstractTestEntitiesHolder(Comparator<I> idComparator, BiFunction<I, Integer, I> fakeIdGenerator) {
        this.idComparator = idComparator;
        this.fakeIdGenerator = fakeIdGenerator;
    }

    @Override
    public void setEntities(Collection<E> entities) {
        if(entities == null || entities.isEmpty()) {
            throw new TestException("Entities can't be empty");
        }
        this.entities.clear();
        this.entities.addAll(entities);
        generateFakeIds();
    }

    @Override
    public List<E> getEntities() {
        if(entities.isEmpty()) {
            throw new TestException("Entities aren't initialized");
        }
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void setFakeEntities(Collection<E> fakeEntities) {
        if(fakeEntities == null || fakeEntities.isEmpty()) {
            throw new TestException("Fake entities can't be empty");
        }
        this.fakeEntities.clear();
        this.fakeEntities.addAll(fakeEntities);
        generateFakeIds();
    }

    @Override
    public List<E> getFakeEntities() {
        if(entities.isEmpty()) {
            throw new TestException("Fake entities aren't initialized");
        }
        return Collections.unmodifiableList(fakeEntities);
    }

    private void generateFakeIds() {
        if(entities.isEmpty() || fakeEntities.isEmpty()) {
            return;
        }
        if(entities.stream()
                .map(Persistent::getId)
                .anyMatch(Objects::isNull)) {
            return;
        }
        Optional<I> maxId = entities.stream()
                .map(Persistent::getId)
                .max(idComparator);
        if(maxId.isPresent()) {
            for(int i = 0; i < fakeEntities.size(); i++) {
                fakeEntities.get(i).setId(fakeIdGenerator.apply(maxId.get(), i + 1));
            }
        }
        else {
            throw new TestException("Max id should be exist to generate fake ids");
        }
    }
}
