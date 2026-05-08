package de.teamplanner.service;

import de.teamplanner.dto.TodoFilterDTO;
import de.teamplanner.exception.EntityNotFoundException;
import de.teamplanner.model.Todo;
import de.teamplanner.repository.TodoRepository;
import de.teamplanner.specification.TodoSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;

    /**
     * Alle Todos.
     */
    public List<Todo> alle() {
        return todoRepository.findAll();
    }

    /**
     * Todos gefiltert.
     */
    public List<Todo> mitFilter(TodoFilterDTO filter) {
        return todoRepository.findAll(TodoSpecification.withFilter(filter));
    }

    /**
     * Alle offenen Todos, sortiert nach Fälligkeit.
     */
    public List<Todo> alleOffen() {
        return todoRepository.findByErledigtFalseOrderByFaelligAmAsc();
    }

    /**
     * Alle erledigten Todos.
     */
    public List<Todo> alleErledigt() {
        return todoRepository.findByErledigtTrue();
    }

    /**
     * Todo anhand ID suchen.
     */
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    /**
     * Todo anhand ID oder Exception.
     */
    public Todo findByIdOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Todo", id));
    }

    /**
     * Todo anlegen oder aktualisieren.
     */
    @Transactional
    public Todo speichern(Todo todo) {
        log.debug("Speichere Todo: {}", todo.getTitel());
        return todoRepository.save(todo);
    }

    /**
     * Erledigungsstatus eines Todos umschalten.
     */
    @Transactional
    public Todo erledigtToggle(Long id) {
        Todo todo = findByIdOrThrow(id);
        todo.setErledigt(!todo.isErledigt());
        todo.setErledigtAm(todo.isErledigt() ? LocalDateTime.now() : null);
        return todoRepository.save(todo);
    }

    /**
     * Todo löschen.
     */
    @Transactional
    public void loeschen(Long id) {
        log.debug("Lösche Todo mit ID {}", id);
        todoRepository.deleteById(id);
    }

    public boolean isUeberfaellig(Todo todo) {
        return todo.getFaelligAm() != null
                && todo.getFaelligAm().isBefore(LocalDate.now())
                && !todo.isErledigt();
    }

    public List<Todo> heuteFaellig() {
        return todoRepository.findByFaelligAmAndErledigtFalse(LocalDate.now());
    }

    public long anzahlOffen() {
        return todoRepository.countByErledigtFalse();
    }

    public long anzahlUeberfaellig() {
        return todoRepository.countByFaelligAmBeforeAndErledigtFalse(LocalDate.now());
    }

    public long anzahlHeuteFaellig() {
        return todoRepository.countByFaelligAmAndErledigtFalse(LocalDate.now());
    }
}
