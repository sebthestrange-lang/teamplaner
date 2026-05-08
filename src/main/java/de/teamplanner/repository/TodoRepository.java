package de.teamplanner.repository;

import de.teamplanner.model.Todo;
import de.teamplanner.model.enums.TodoPrioritaet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>,
        JpaSpecificationExecutor<Todo> {

    List<Todo> findByErledigtFalseOrderByFaelligAmAsc();

    List<Todo> findByErledigtTrue();

    List<Todo> findByFaelligAmBeforeAndErledigtFalse(LocalDate datum);

    List<Todo> findByFaelligAmAndErledigtFalse(LocalDate datum);

    List<Todo> findByPrioritaet(TodoPrioritaet prioritaet);

    long countByErledigtFalse();

    long countByFaelligAmBeforeAndErledigtFalse(LocalDate datum);

    long countByFaelligAmAndErledigtFalse(LocalDate datum);
}
