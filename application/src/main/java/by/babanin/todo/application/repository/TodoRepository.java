package by.babanin.todo.application.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("select t from todos t where t.priority in :priorities")
    List<Todo> findAllByPriorities(@Param("priorities") Collection<Priority> priorities);
}
