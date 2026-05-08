package de.teamplanner.service;

import de.teamplanner.dto.MitarbeiterFilterDTO;
import de.teamplanner.exception.EntityNotFoundException;
import de.teamplanner.model.Mitarbeiter;
import de.teamplanner.model.Team;
import de.teamplanner.repository.MitarbeiterRepository;
import de.teamplanner.specification.MitarbeiterSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MitarbeiterService {

    private static final Logger log = LoggerFactory.getLogger(MitarbeiterService.class);

    private final MitarbeiterRepository mitarbeiterRepository;

    /**
     * Alle Mitarbeiter.
     */
    public List<Mitarbeiter> alleMitarbeiter() {
        return mitarbeiterRepository.findAll();
    }

    /**
     * Mitarbeiter gefiltert.
     */
    public List<Mitarbeiter> mitFilter(MitarbeiterFilterDTO filter) {
        return mitarbeiterRepository.findAll(MitarbeiterSpecification.withFilter(filter));
    }

    /**
     * Alle Mitarbeiter eines Teams.
     */
    public List<Mitarbeiter> findByTeam(Team team) {
        return mitarbeiterRepository.findByTeamOrderByNachnameAsc(team);
    }

    /**
     * Mitarbeiter anhand ID suchen.
     */
    public Optional<Mitarbeiter> findById(Long id) {
        return mitarbeiterRepository.findById(id);
    }

    /**
     * Mitarbeiter anhand ID oder Exception.
     */
    public Mitarbeiter findByIdOrThrow(Long id) {
        return mitarbeiterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mitarbeiter", id));
    }

    /**
     * Mitarbeiter anlegen oder aktualisieren.
     */
    @Transactional
    public Mitarbeiter speichern(Mitarbeiter mitarbeiter) {
        log.debug("Speichere Mitarbeiter: {} {}", mitarbeiter.getVorname(), mitarbeiter.getNachname());
        return mitarbeiterRepository.save(mitarbeiter);
    }

    /**
     * Mitarbeiter löschen.
     */
    @Transactional
    public void loeschen(Long id) {
        log.debug("Lösche Mitarbeiter mit ID {}", id);
        mitarbeiterRepository.deleteById(id);
    }

    /**
     * Alle Mitarbeiter, die noch nicht in diesem Team sind (für Zuweisung).
     */
    public List<Mitarbeiter> findNichtImTeam(Team team) {
        return mitarbeiterRepository.findByTeamIsNullOrTeamNotOrderByNachnameAsc(team);
    }

    /**
     * Mitarbeiter einem Team zuweisen.
     */
    @Transactional
    public void zuTeamZuweisen(Long mitarbeiterId, Team team) {
        Mitarbeiter mitarbeiter = findByIdOrThrow(mitarbeiterId);
        mitarbeiter.setTeam(team);
        mitarbeiterRepository.save(mitarbeiter);
        log.debug("Mitarbeiter {} dem Team {} zugewiesen", mitarbeiter.getVollstaendigerName(), team.getName());
    }

    /**
     * Mitarbeiter aus seinem Team entfernen.
     */
    @Transactional
    public void ausTeamEntfernen(Long mitarbeiterId) {
        Mitarbeiter mitarbeiter = findByIdOrThrow(mitarbeiterId);
        mitarbeiter.setTeam(null);
        mitarbeiterRepository.save(mitarbeiter);
        log.debug("Mitarbeiter {} aus Team entfernt", mitarbeiter.getVollstaendigerName());
    }

    public long anzahl() {
        return mitarbeiterRepository.count();
    }
}
