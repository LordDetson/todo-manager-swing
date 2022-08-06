package by.babanin.todo.application.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import by.babanin.todo.model.Priority;

@Repository
public interface PriorityRepository extends IndexableRepository<Priority, Long> {

    Optional<Priority> findByName(String name);
}
