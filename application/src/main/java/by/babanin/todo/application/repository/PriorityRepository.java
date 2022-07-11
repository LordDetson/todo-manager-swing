package by.babanin.todo.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.babanin.todo.model.Priority;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {

}
