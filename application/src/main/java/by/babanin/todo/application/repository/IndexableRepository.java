package by.babanin.todo.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;

@NoRepositoryBean
public interface IndexableRepository<E extends Persistent<I> & Indexable, I> extends JpaRepository<E, I> {

    Optional<E> findByPosition(long position);

    List<E> findByPositionGreaterThanOrderByPositionAsc(long position);
}
