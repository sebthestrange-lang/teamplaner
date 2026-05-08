package de.teamplanner.service;

import de.teamplanner.exception.EntityNotFoundException;
import de.teamplanner.model.Team;
import de.teamplanner.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    /**
     * Alle Teams alphabetisch sortiert.
     */
    public List<Team> alleTeams() {
        return teamRepository.findAll();
    }

    /**
     * Team anhand ID suchen.
     */
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    /**
     * Team anhand ID oder Exception.
     */
    public Team findByIdOrThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team", id));
    }

    /**
     * Team anlegen oder aktualisieren.
     */
    @Transactional
    public Team speichern(Team team) {
        log.debug("Speichere Team: {}", team.getName());
        return teamRepository.save(team);
    }

    /**
     * Team löschen.
     */
    @Transactional
    public void loeschen(Long id) {
        log.debug("Lösche Team mit ID {}", id);
        teamRepository.deleteById(id);
    }

    public long anzahl() {
        return teamRepository.count();
    }
}
